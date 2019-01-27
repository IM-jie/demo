package com.alibaba.datax.core.util;

import com.alibaba.datax.common.exception.DataXException;
import com.alibaba.datax.common.util.Configuration;
import com.alibaba.datax.core.util.container.CoreConstant;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public final class ConfigParser {
    private static final Logger LOG = LoggerFactory.getLogger(ConfigParser.class);
    /**
     * 指定Job配置路径，ConfigParser会解析Job、Plugin、Core全部信息，并以Configuration返回
     */
    public static Configuration parse(final String jobContent) {
        Configuration configuration = ConfigParser.parseJobConfig(jobContent);

        configuration.merge(
                ConfigParser.parseCoreConfig(CoreConstant.DATAX_CONF_PATH),
                false);
        // todo config优化，只捕获需要的plugin
        String readerPluginName = configuration.getString(
                CoreConstant.DATAX_JOB_CONTENT_READER_NAME);
        String writerPluginName = configuration.getString(
                CoreConstant.DATAX_JOB_CONTENT_WRITER_NAME);

        String preHandlerName = configuration.getString(
                CoreConstant.DATAX_JOB_PREHANDLER_PLUGINNAME);

        String postHandlerName = configuration.getString(
                CoreConstant.DATAX_JOB_POSTHANDLER_PLUGINNAME);

        Set<String> pluginList = new HashSet<String>();
        pluginList.add(readerPluginName);
        pluginList.add(writerPluginName);

        if(StringUtils.isNotEmpty(preHandlerName)) {
            pluginList.add(preHandlerName);
        }
        if(StringUtils.isNotEmpty(postHandlerName)) {
            pluginList.add(postHandlerName);
        }
        List<String> readerPlugins = new ArrayList<String>();
        List<String> writerplugins = new ArrayList<String>();
        Map<String, List<String>> pluginMap = new HashMap<String, List<String>>(2);

        readerPlugins.add("{" +
                "    \"name\": \"mysqlreader\"," +
                "    \"class\": \"com.alibaba.datax.plugin.reader.mysqlreader.MysqlReader\"" +
                "}");
        writerplugins.add("{" +
                "    \"name\": \"mysqlwriter\"," +
                "    \"class\": \"com.alibaba.datax.plugin.writer.mysqlwriter.MysqlWriter\"" +
                "}");
        pluginMap.put("reader", readerPlugins);
        pluginMap.put("writer", writerplugins);
        LOG.info("parsePlugin--------->" + parsePluginConfigByJson(pluginMap));

        try {
//            configuration.merge(parsePluginConfig(new ArrayList<String>(pluginList)), false);
            configuration.merge(parsePluginConfigByJson(pluginMap), false);
        }catch (Exception e){
            //吞掉异常，保持log干净。这里message足够。
            LOG.warn(String.format("插件[%s,%s]加载失败，1s后重试... Exception:%s ", readerPluginName, writerPluginName, e.getMessage()));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e1) {
                //
            }
            configuration.merge(parsePluginConfig(new ArrayList<String>(pluginList)), false);
        }

        return configuration;
    }

    private static Configuration parsePluginConfigByJson(Map<String, List<String>> pluginMap){
        Configuration configuration = Configuration.newDefault();
        for (Map.Entry<String, List<String>> plugins : pluginMap.entrySet()){
            for (String pluginJson : plugins.getValue()){
                String pluginName = Configuration.from(pluginJson).getString("name");
                configuration.set(
                        String.format("plugin.%s.%s", plugins.getKey(), pluginName),
                        Configuration.from(pluginJson).getInternal());
            }

        }
        return configuration;
    }

    private static Configuration parseCoreConfig(final String path) {
        return Configuration.from(new File(path));
    }

    public static Configuration parseJobConfig(final String jobContent) {
        //移除jobPath
        //String jobContent = getJobContent(path);
        Configuration config = Configuration.from(jobContent);

        return SecretUtil.decryptSecretKey(config);
    }


    public static Configuration parsePluginConfig(List<String> wantPluginNames) {
        Configuration configuration = Configuration.newDefault();

        Set<String> replicaCheckPluginSet = new HashSet<String>();
        int complete = 0;
        for (final String each : ConfigParser
                .getDirAsList(CoreConstant.DATAX_PLUGIN_READER_HOME)) {
            LOG.info("plugin dir-->" + each);
            Configuration eachReaderConfig = ConfigParser.parseOnePluginConfig(each, "reader", replicaCheckPluginSet, wantPluginNames);
            if(eachReaderConfig!=null) {
                LOG.info("eachReadconfig-->" + eachReaderConfig.toJSON());
                configuration.merge(eachReaderConfig, true);
                complete += 1;
            }
        }

        for (final String each : ConfigParser
                .getDirAsList(CoreConstant.DATAX_PLUGIN_WRITER_HOME)) {
            Configuration eachWriterConfig = ConfigParser.parseOnePluginConfig(each, "writer", replicaCheckPluginSet, wantPluginNames);
            if(eachWriterConfig!=null) {
                configuration.merge(eachWriterConfig, true);
                complete += 1;
            }
        }

        if (wantPluginNames != null && wantPluginNames.size() > 0 && wantPluginNames.size() != complete) {
            throw DataXException.asDataXException(FrameworkErrorCode.PLUGIN_INIT_ERROR, "插件加载失败，未完成指定插件加载:" + wantPluginNames);
        }

        LOG.info("configuration--->" + configuration.toJSON());
        return configuration;
    }


    public static Configuration parseOnePluginConfig(final String path,
                                                     final String type,
                                                     Set<String> pluginSet, List<String> wantPluginNames) {
        String filePath = path + File.separator + "plugin.json";
        Configuration configuration = Configuration.from(new File(filePath));

        String pluginPath = configuration.getString("path");
        String pluginName = configuration.getString("name");
        if(!pluginSet.contains(pluginName)) {
            pluginSet.add(pluginName);
        } else {
            throw DataXException.asDataXException(FrameworkErrorCode.PLUGIN_INIT_ERROR, "插件加载失败,存在重复插件:" + filePath);
        }

        //不是想要的插件，返回null
        if (wantPluginNames != null && wantPluginNames.size() > 0 && !wantPluginNames.contains(pluginName)) {
            return null;
        }

        boolean isDefaultPath = StringUtils.isBlank(pluginPath);
        if (isDefaultPath) {
            configuration.set("path", path);
        }

        Configuration result = Configuration.newDefault();
        LOG.info("pluginName-->" + pluginName);
        LOG.info("configuration getInternal--->" + configuration.toJSON());
        LOG.info("configuration getInternal--->" + configuration.getInternal());
        LOG.info("String format--->" + String.format("plugin.%s.%s", type, pluginName));
        result.set(
                String.format("plugin.%s.%s", type, pluginName),
                configuration.getInternal());

        return result;
    }

    private static List<String> getDirAsList(String path) {
        List<String> result = new ArrayList<String>();

        String[] paths = new File(path).list();
        if (null == paths) {
            return result;
        }

        for (final String each : paths) {
            result.add(path + File.separator + each);
        }

        return result;
    }

}
