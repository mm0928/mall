package com.leyou.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 跨域配置-CORS的跨域过滤器，解决跨域问题
 */
@Configuration
public class GlobalCorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        /**
         * 一、添加CORS配置信息
         */
        CorsConfiguration config = new CorsConfiguration();
        /**
         * 1、允许的域,不要写*，否则cookie就无法使用了
         */
        config.addAllowedOrigin("http://manage.leyou.com");
        config.addAllowedOrigin("http://api.leyou.com");
        config.addAllowedOrigin("http://localhost:9002");
        config.addAllowedOrigin("http://localhost:10010");
        /**
         * 允许的ip
         */
        config.addAllowedOrigin("http://127.0.0.1:9002");
        config.addAllowedOrigin("http://127.0.0.1:10010");
        /**
         * 2、是否发送Cookie信息
         */
        config.setAllowCredentials(true);
        /**
         * 3、允许的请求方式
         */
        config.addAllowedMethod("OPTIONS");
        config.addAllowedMethod("HEAD");
        config.addAllowedMethod("GET");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("DELETE");
        config.addAllowedMethod("PATCH");
        /**
         * 4、允许的头信息
         */
        config.addAllowedHeader("*");
        /**
         * 二、添加映射路径，我们拦截一切请求
         */
        UrlBasedCorsConfigurationSource configSource = new UrlBasedCorsConfigurationSource();
        configSource.registerCorsConfiguration("/**", config);

        //输出configSource
        System.out.println("configSource:" + configSource.toString());

        /**
         * 三、返回新的CorsFilter
         */

        System.out.println(configSource.toString());
        return new CorsFilter(configSource);
    }
}
