package com.xjx.mydns.utils;

import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 常用类型转换
 */
@Component
public class CommonUtils {

    /**
     * LinkedHashMap 转化为具体对象
     * @param valueMap
     * @param objClazz
     * @param <T>
     * @return
     */
    public static <T> T linkedHashMapToBean(LinkedHashMap<String,Object> valueMap, Class<T> objClazz){
        T t = null;
        //反射创建对象
        try {
            Constructor<T> constructor = objClazz.getConstructor();
            constructor.setAccessible(true);
            t = constructor.newInstance();
            Field[] fields = objClazz.getDeclaredFields();
            int index = 0;
            for(Map.Entry<String,Object> entry: valueMap.entrySet()){
                fields[index].setAccessible(true);
                fields[index++].set(t,entry.getValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }

    // 时间格式转换
    public Integer parseTimeFormat(String time){
        String[] splits = time.split(":");
        String ans = splits[0]+splits[1]+splits[2];
        return Integer.valueOf(ans);
    }

    // Object转json
    public String convertObjectToJson(Object obj){
        if(obj == null)
            return null;
        return JSON.toJSONString(obj);
    }

    // json转Object
    public <T> T convertJsonToObject(String json, Class<T> clazz){
        if(json==null || json=="")
            return null;
        if(clazz==int.class || clazz==Integer.class)
            return (T)Integer.valueOf(json);
        else if(clazz==long.class || clazz==Long.class)
            return (T)Long.valueOf(json);
        else
            return JSON.parseObject(json,clazz);
    }
}
