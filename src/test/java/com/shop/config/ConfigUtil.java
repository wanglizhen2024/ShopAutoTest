package com.shop.config;

import java.io.InputStream;
import java.util.Properties;

public class ConfigUtil {
    private static Properties props = new Properties();

    static {
        try (InputStream in = ConfigUtil.class.getClassLoader().getResourceAsStream("config.properties")) {
            props.load(in);
        } catch (Exception e) {
            throw new RuntimeException("配置文件加载失败！", e);
        }
    }

    public static String getValue(String key) {
        return props.getProperty(key);
    }
}