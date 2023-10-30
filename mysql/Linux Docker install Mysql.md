#### Linux Docker install Mysql

##### 1. Get Version

```shell
docker pull mysql:8.0.20
## or
docker pull mysql
```



##### 2.configuration

```shell
### docker images
docker images

dm8_single   dm8_20230808_rev197096_x86_rh6_64   ab98e2f4a568   2 months ago    601MB
openjdk      8-jdk-slim-buster                   977be15c64f8   14 months ago   288MB
nginx        stable-alpine                       ccb911fdd2ca   14 months ago   23.5MB
redis        5.0.14-alpine3.15                   a70d80b7cdb0   18 months ago   29.4MB
mysql8.0     8.0.20                              be0dbf01a0f3   3 years ago     541MB


### setting my.in 
touch /root/mysql-8.0/my.cnf
vim my.cnf

## context

er=mysql
character-set-server=utf8
default_authentication_plugin=mysql_native_password
secure_file_priv=/var/lib/mysql
expire_logs_days=7
sql_mode=STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION
max_connections=1000

[client]
default-character-set=utf8

[mysql]
default-character-set=utf8
secure_file_priv=/var/lib/mysql

### docker run
docker run \
--name mysql-8.0 \
-d \
-p 3307:3306 \
--restart unless-stopped \
-v /root/mysql-8.0/log:/var/log/mysql \
-v /root/mysql-8.0/data:/var/lib/mysql \
-v /root/mysql-8.0/conf:/etc/mysql \
-v /root/mysql-8.0/mysql-files:/var/lib/mysql-files  \
-e MYSQL_ROOT_PASSWORD=eureka,123 \
-e TZ=Asia/Shanghai mysql8.0:8.0.20 \
--skip-name-resolve --lower_case_table_names=1


## -p 映射主机端口：容器端口
## restart 容器重启策略
## -v 映射8.0的主机目录：容器目录（log data等）
## -e MYSQL_ROOT_PASSWORD 初始化密码
## -e TZ 上海时区
## --skip-name-resolve 禁用DNS反向解析配置
## --lower_case_table_names 忽略大小写


## Docker container 
docker ps 

## Enter the container command line
docker exec -it mysql-8.0 /bin/bash
## free login
mysql -uroot
# show database
show databases;
#use mysql
use mysql;
#set password
#PASSWORD EXPIRE NEVER 密码永不过期
#mysql_native_password 加密插件
select user,host,plugin,authentication_string from mysql.user;
ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY '123456' PASSWORD EXPIRE NEVER;

create user 'root'@'%' IDENTIFIED  BY 'eureka,123';
ALTER USER'root'@'%' IDENTIFIED WITH mysql_native_password BY 'eureka,123'  PASSWORD EXPIRE NEVER;

grant all privileges on *.* to 'root'@'%'
flush privileges;

## 常用命令
docker ps
docker images
docker logs #id
docker rm #id
docker stop #name
docker start #name


```



