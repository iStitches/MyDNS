package com.xjx.mydns.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 请求拦截配置管理
 */
@Configuration
public class WebMvcConfigure implements WebMvcConfigurer {
    // 配置跨域资源映射
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/analysis/**")
//                .allowedHeaders("Access-Control-Allow-Origin")
//                .allowedMethods("POST","GET")
//                .allowedOrigins("*");
//    }
}
