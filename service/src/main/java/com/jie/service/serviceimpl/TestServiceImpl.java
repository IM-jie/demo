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
                "  \"job\": {\n" +
                "    \"content\": [\n" +
                "      {\n" +
                "        \"reader\": {\n" +
                "          \"name\": \"streamreader\",\n" +
                "          \"parameter\": {\n" +
                "            \"sliceRecordCount\": 1,\n" +
                "            \"column\": [\n" +
                "              {\n" +
                "                \"type\": \"long\",\n" +
                "                \"value\": \"10\"\n" +
                "              },\n" +
                "              {\n" +
                "                \"type\": \"string\",\n" +
                "                \"value\": \"hello，你好，世界-DataX\"\n" +
                "              }\n" +
                "            ]\n" +
                "          }\n" +
                "        },\n" +
                "        \"writer\": {\n" +
                "          \"name\": \"streamwriter\",\n" +
                "          \"parameter\": {\n" +
                "            \"encoding\": \"UTF-8\",\n" +
                "            \"print\": true\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    ],\n" +
                "    \"setting\": {\n" +
                "      \"speed\": {\n" +
                "        \"channel\": 1\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
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
