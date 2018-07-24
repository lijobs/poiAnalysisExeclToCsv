package com.poianalysisexecltocsv.demo.util.constantsutil;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author lizehua 2018-07-24 14:08 pm
 * @desc 读取config.properties文件中的配置信息
 */
public class ConfigUtil {

    /**
     * 通过config.properties文件中的key值获取配置信息
     *
     * @param key config.properties文件中的key值
     * @return 返回key对应的内容
     */
    public static String getConfigToKey(String key) {
        Properties properties = new Properties();
        InputStream inputStream = ConfigUtil.class.getClassLoader().getResourceAsStream("config.properties");
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties.getProperty(key);
    }


}
