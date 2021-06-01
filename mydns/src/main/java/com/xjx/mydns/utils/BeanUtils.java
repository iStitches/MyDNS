package com.xjx.mydns.utils;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 获取BeanFactory对象来生成指定的Bean对象
 */
public class BeanUtils {
      public static BeanFactory beanFactory;

      public static void init(ApplicationContext applicationContext){
          beanFactory = applicationContext;
      }

      public static <T> T createBean(Class targetClass){
          return (T)beanFactory.getBean(targetClass);
      }
}
