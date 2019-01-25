package com.jie.service.serviceimpl;

import com.alibaba.datax.core.Engine;
import com.jie.service.iservice.ITestService;
import org.springframework.stereotype.Service;

@Service
public class TestServiceImpl implements ITestService {
    @Override
    public String test() {
        System.setProperty("datax.home", getCurrentClasspath());
        String jobContent = "{\n" +
                "    \"job\": {\n" +
                "        \"content\": [\n" +
                "            {\n" +
                "\n" +
                "            \"reader\": {\n" +
                "                    \"name\": \"mysqlreader\",\n" +
                "                    \"parameter\": {\n" +
                "                        \"username\": \"root\",\n" +
                "                        \"password\": \"123456\",\n" +
                "                        \"column\": [\"*\"],\n" +
                "                        \"splitPk\": \"id\",\n" +
                "                        \"connection\": [\n" +
                "                            {\n" +
                "                                \"table\": [\n" +
                "                                    \"t_test\"\n" +
                "                                ],\n" +
                "                                \"jdbcUrl\": [\n" +
                "                                    \"jdbc:mysql://134.175.21.91:3306/datasource?useUnicode=true&characterEncoding=utf8\"\n" +
                "                                ]\n" +
                "                            }\n" +
                "                        ]\n" +
                "                    }\n" +
                "                },\n" +
                "                \"writer\": {\n" +
                "                    \"name\": \"mysqlwriter\",\n" +
                "                    \"parameter\": {\n" +
                "                        \"writeMode\": \"insert\",\n" +
                "                        \"username\": \"root\",\n" +
                "                        \"password\": \"123456\",\n" +
                "                        \"column\": [\n" +
                "                           \"*\"\n" +
                "                        ],\n" +
                "                        \"session\": [\n" +
                "                            \"set session sql_mode='ANSI'\"\n" +
                "                        ],\n" +
                "                        \"preSql\": [\n" +
                "                            \"truncate test\"\n" +
                "                        ],\n" +
                "                        \"connection\": [\n" +
                "                            {\n" +
                "                                \"jdbcUrl\": \"jdbc:mysql://134.175.21.91:3306/datatarget?useUnicode=true&characterEncoding=utf8\",\n" +
                "                                \"table\": [\n" +
                "                                    \"test\"\n" +
                "                                ]\n" +
                "                            }\n" +
                "                        ]\n" +
                "                    }\n" +
                "                    \n" +
                "                }\n" +
                "            }\n" +
                "        ],\n" +
                "        \"setting\": {\n" +
                "            \"speed\": {\n" +
                "                \"channel\": 5\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "}";

        try {
            Engine.entry("standalone", jobContent, "-1");
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return System.getProperty("datax.home");
    }

    public static String getCurrentClasspath() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String currentClasspath = classLoader.getResource("").getPath();
        // 当前操作系统
        String osName = System.getProperty("os.name");
        if (osName.startsWith("Windows")) {
            // 删除path中最前面的/
            currentClasspath = currentClasspath.substring(1);
        }
        return currentClasspath;
    }
}
