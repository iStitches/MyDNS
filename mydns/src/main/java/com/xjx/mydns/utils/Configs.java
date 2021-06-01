package com.xjx.mydns.utils;


import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * 读取配置文件工具类
 */
public class Configs {
    static Environment env;

    public static void init(ApplicationContext applicationContext){
         env = applicationContext.getEnvironment();
    }

    public static String get(String key){
        String val = env.getProperty(key);
        if(val != null)
            return val.trim();
        else
            return null;
    }

    public static int getInt(String key){
        String val = env.getProperty(key);
        return Integer.valueOf(val);
    }
}
