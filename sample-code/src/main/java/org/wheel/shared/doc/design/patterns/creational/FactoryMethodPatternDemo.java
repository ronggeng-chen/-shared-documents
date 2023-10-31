package org.wheel.shared.doc.design.patterns.creational;

/**
 * -shared-documents
 *
 * @Author: aGeng
 * @Description: 工厂方法模式
 * @Target:
 * @Date Created in 2023-10-31-10:58
 * @Modified By:
 */
public class FactoryMethodPatternDemo {

    interface IDataSetParser {
        String toSql();

        Object toEntity();
    }


    class DataSetViewParser implements IDataSetParser {
        public String toSql() {
            // parser view ....
            return null;
        }

        public Object toEntity() {
            return null;
        }
    }


    class DataSetFileParser implements IDataSetParser {
        public String toSql() {
            // parser file .....
            return null;
        }

        public Object toEntity() {
            return null;
        }
    }

    class StoreFactory {

        public IDataSetParser getSqlParser(String type) {
            if ("file".equals(type)) {
                return new DataSetFileParser();
            } else if ("view".equals(type)) {
                return new DataSetViewParser();
            }
            throw new RuntimeException("不存在的类型");
        }

    }

}
