``` java
package com.test.api.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * 
 * redist拓展操作工具类
 *
 * @Author: aGeng
 * @Description:
 * @Target:
 * @Date Created in 2023-10-20-11:27
 * @Modified By:
 */
@Slf4j
public class RedisExpUtil {
    /**
     * 获取Key完整的RedisKey
     * 此方法为 CacheOperatorExpApi 实现类注入Bean，RedisKey规则，区别于Guns提供的CacheOperatorApi前缀规则为此方法不对Key做统一的大小写处理
     *
     * @param prefix 前缀
     * @param key    处理前Key
     * @return
     */
    public static String getPrefix(String prefix, String key) {
        return StrUtil.isBlank(prefix) ? key : prefix + key;
    }

    /**
     * 批量写入Redis数据
     *
     * @param redisTemplate redisTemplate Bean
     * @param maps          数据
     * @param <T>           写入数据value类型
     */
    public static <T> void writeBatch(RedisTemplate<String, T> redisTemplate, Map<String, T> maps) {
        writeBatch(redisTemplate, null, maps);
    }

    /**
     * 批量写入Redis数据
     * 使用executePipelined使一批命令集打包成一个管道，一次性提交给Redis服务器
     *
     * @param redisTemplate redisTemplate Bean
     * @param prefix        前缀
     * @param maps          数据
     * @param <T>           写入数据value类型
     */
    public static <T> void writeBatch(RedisTemplate<String, T> redisTemplate, String prefix, Map<String, T> maps) {

        RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
        RedisSerializer<T> valueSerializer = (RedisSerializer<T>) redisTemplate.getValueSerializer();
        //注意：使用executePipelined使一批命令集打包成一个管道，一次性提交给Redis服务器（execute会等待前一个命令虽然是一个事务但是在有慢速命令情况会导致整个操作变慢）
        //手动控制connection.openPipeline() connection.closePipeline() 多线程下互相抢夺控制权会导致Redis Read Time Out
        redisTemplate.executePipelined(new RedisCallback<String>() {
            @Override
            public String doInRedis(RedisConnection redisConnection) throws DataAccessException {
                maps.forEach((toKey, toValue) -> {
                    if (toKey == null) {
                        return;
                    }
                    byte[] keyByte = serializer.serialize(getPrefix(prefix, toKey));
                    byte[] valueByte = valueSerializer.serialize(toValue);

                    if (keyByte != null && valueByte != null) {
                        redisConnection.set(keyByte, valueByte);
                    }
                });
                return null;
            }
        });

    }

    /**
     * 批量查询Redis数据
     *
     * @param redisTemplate redisTemplate Bean
     * @param keys          待查询Key的集合
     * @param <T>           查询数据value类型
     * @return
     */
    public static <T> Map<String, T> readBatch(RedisTemplate<String, T> redisTemplate, List<String> keys) {
        return readBatch(redisTemplate, null, keys);
    }


    /**
     * 批量查询Redis数据
     *
     * @param redisTemplate redisTemplate Bean
     * @param prefix        前缀
     * @param keys          待查询Key的集合
     * @param <T>           查询数据value类型
     * @return
     */
    public static <T> Map<String, T> readBatch(RedisTemplate<String, T> redisTemplate, String prefix, List<String> keys) {
        Map<String, T> result = new HashMap<>();
        RedisSerializer<T> valueSerializer = (RedisSerializer<T>) redisTemplate.getValueSerializer();
        RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
        //注意：使用executePipelined使一批命令集打包成一个管道，一次性提交给Redis服务器（execute会等待前一个命令虽然是一个事务但是在有慢速命令情况会导致整个操作变慢）
        //手动控制connection.openPipeline() connection.closePipeline() 多线程下互相抢夺控制权会导致Redis Read Time Out
        List<Object> redisResult = redisTemplate.executePipelined((RedisCallback<Map<String, T>>) connection -> {
            // 使用管道
            // 执行命令
            for (String key : keys) {
                connection.get(serializer.serialize(getPrefix(prefix, key)));
            }
            // 获取命令的结果
            return null;
        }, valueSerializer);
        for (int i = 0; i < keys.size(); i++) {
            T value = (T) redisResult.get(i);
            result.put(keys.get(i), value);
        }
        return result;
    }

    /**
     * 批量查询Redis多线程任务
     *
     * @param prefix    前缀
     * @param keys      待查询key集合
     * @param partition 以多少个Key查询分割线程查询
     * @param <T>       查询数据value类型
     * @return
     */
    public static <T> Map<String, T> readBatchTask(String prefix, List<String> keys, Integer partition) {
        RedisTemplate<String, T> redisTemplate = SpringUtil.getBean("objectRedisTemplate", RedisTemplate.class);
        return readBatchTask(redisTemplate, prefix, keys, partition);
    }

    /**
     * 批量查询Redis多线程任务
     *
     * @param redisTemplate redisTemplate Bean
     * @param prefix        前缀
     * @param keys          待查询key集合
     * @param partition     以多少个Key查询分割线程查询
     * @param <T>           查询数据value类型
     * @return
     */
    public static <T> Map<String, T> readBatchTask(RedisTemplate<String, T> redisTemplate, String prefix, List<String> keys, Integer partition) {
        Map<String, T> result = new HashMap<>();
        RedisExpUtil.log.info("Redis查询开始 前缀：{}", prefix);
        if (keys.size() <= partition) {
            result = RedisExpUtil.readBatch(redisTemplate, prefix, keys);
        } else {
            ThreadPoolTaskExecutor taskExecutor = SpringUtil.getBean("taskExecutor", ThreadPoolTaskExecutor.class);
            List<Future<Map<String, T>>> futureList = new ArrayList<>();
            List<List<String>> keysPartition = ListUtil.partition(keys, partition);
            for (int i = 0; i < keysPartition.size(); i++) {
                List<String> strings = keysPartition.get(i);
                final int finalI = i;
                Future<Map<String, T>> future = taskExecutor.submit(new Callable<Map<String, T>>() {
                    org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

                    @Override
                    public Map<String, T> call() {
                        Map<String, T> stringTMap = new HashMap<>();
                        try {
                            stringTMap = RedisExpUtil.readBatch(redisTemplate, prefix, strings);
                        } catch (Exception exception) {
                            exception.printStackTrace();
                            logger.error("缓存前缀:{} 批量查询Redis出现错误 msg:{}", prefix, exception.getMessage());
                        }
                        RedisExpUtil.log.info("缓存前缀:{} 缓存队列:{} 是否执行成功：true", prefix, finalI);
                        return stringTMap;
                    }
                });
                futureList.add(future);
            }
            if (CollUtil.isNotEmpty(futureList)) {
                for (Future<Map<String, T>> mapFuture : futureList) {
                    try {
                        Map<String, T> res = mapFuture.get();
                        if (MapUtil.isNotEmpty(res)) {
                            result.putAll(res);
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
            }
        }
        RedisExpUtil.log.info("Redis查询结束 前缀：{}", prefix);
        return result;
    }


    /**
     * 批量写入Redis多线程任务
     *
     * @param prefix    前缀
     * @param maps      待写入Map
     * @param partition 以多少个Key查询分割线程查询
     * @param <T>       查询数据value类型
     * @return
     */
    public static <T> void writeBatchTask(String prefix, Map<String, T> maps, Integer partition) {
        RedisTemplate<String, T> redisTemplate = SpringUtil.getBean("objectRedisTemplate", RedisTemplate.class);
        writeBatchTask(redisTemplate, prefix, maps, partition);
    }

    /**
     * 批量写入Redis多线程任务
     *
     * @param redisTemplate redisTemplate Bean
     * @param prefix        前缀
     * @param maps          待写入Map
     * @param partition     以多少个Key查询分割线程查询
     * @param <T>           查询数据value类型
     * @return
     */
    public static <T> void writeBatchTask(RedisTemplate<String, T> redisTemplate, String prefix, Map<String, T> maps, Integer partition) {
        RedisExpUtil.log.info("Redis缓存开始 前缀：{}", prefix);
        if (maps.keySet().size() <= partition) {
            RedisExpUtil.writeBatch(redisTemplate, prefix, maps);
        } else {
            ThreadPoolTaskExecutor taskExecutor = SpringUtil.getBean("taskExecutor", ThreadPoolTaskExecutor.class);
            List<Future<String>> futureList = new ArrayList<>();
            List<Map<String, T>> partitionList = partition(maps, partition);
            for (int i = 0; i < partitionList.size(); i++) {
                Map<String, T> currMaps = partitionList.get(i);
                final int finalI = i;
                Future<String> future = taskExecutor.submit(new Callable<String>() {
                    org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

                    @Override
                    public String call() {
                        Boolean isSuccess = true;
                        try {
                            RedisExpUtil.writeBatch(redisTemplate, prefix, currMaps);
                        } catch (Exception exception) {
                            exception.printStackTrace();
                            logger.error("缓存前缀:{} 批量缓存Redis出现错误 msg:{}", prefix, exception.getMessage());
                            isSuccess = false;
                        }
                        return StrUtil.format("缓存前缀:{} 缓存队列:{} 是否执行成功：{}", prefix, finalI, isSuccess);
                    }
                });
                futureList.add(future);
            }
            if (CollUtil.isNotEmpty(futureList)) {
                for (Future<String> mapFuture : futureList) {
                    try {
                        String msg = mapFuture.get();
                        RedisExpUtil.log.info("{}", msg);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
            }
        }
        RedisExpUtil.log.info("缓存前缀：{} Redis缓存结束", prefix);
    }


    private static <T> List<Map<String, T>> partition(Map<String, T> toMaps, int nums) {
        if (MapUtil.isEmpty(toMaps)) {
            return new ArrayList<>();
        }
        Set<String> keySet = toMaps.keySet();
        Iterator<String> iterator = keySet.iterator();
        int i = 1;
        List<Map<String, T>> total = new ArrayList<>();
        Map<String, T> map = new HashMap<>();
        while (iterator.hasNext()) {
            String next = iterator.next();
            map.put(next, toMaps.get(next));
            if (i == nums) {
                total.add(map);
                map = new HashMap<>();
                i = 0;
            }
            i++;
        }
        if (CollUtil.isNotEmpty(map)) {
            total.add(map);
        }
        return total;
    }

    public static boolean containsAll(Collection<String> redisKeys, Collection<String> mapKeys) {
        if (CollUtil.isEmpty(redisKeys)) {
            return CollUtil.isEmpty(mapKeys);
        }

        if (CollUtil.isEmpty(mapKeys)) {
            return true;
        }

        if (redisKeys.size() < mapKeys.size()) {
            return false;
        }

        for (String str : mapKeys) {
            if (false == redisKeys.contains(str.toUpperCase())) {
                return false;
            }
        }
        return true;
    }

    public static List<String> subtractToList(Collection<String> mapKeys, Collection<String> redisKeys) {

        if (CollUtil.isEmpty(mapKeys)) {
            return ListUtil.empty();
        }
        if (CollUtil.isEmpty(redisKeys)) {
            return ListUtil.list(true, mapKeys);
        }

        //将被交数用链表储存，防止因为频繁扩容影响性能
        final List<String> result = new LinkedList<>();
        Set<String> set = new HashSet<>(redisKeys);
        for (String t : mapKeys) {
            if (false == set.contains(t.toUpperCase())) {
                result.add(t);
            }
        }
        return result;
    }

    public static void main(String[] args) {
        CollUtil.subtractToList(ListUtil.of("1", "2", "3", "7"), ListUtil.of("1", "2", "3", "5"));
        //输出 7
        CollUtil.subtractToList(ListUtil.of("1", "2", "3"), ListUtil.of("1", "2", "3", "5"));
        //输出 0
        //故：left：为待缓存项，right：为Redis已缓存项
    }
}

```