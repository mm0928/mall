package com.leyou.item;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * 商品微服务
 */
@SpringBootApplication
//Eureka Client,@EnableDiscoveryClient 或@EnableEurekaClient
@EnableDiscoveryClient
//mapper接口的包扫描,以后不需要在mapper接口上声明@Mapper注解
@MapperScan(basePackages = "com.leyou.item.mapper")
@EnableTransactionManagement
public class LyItemService {
    public static void main(String[] args) {
        SpringApplication.run(LyItemService.class, args);
    }
}
