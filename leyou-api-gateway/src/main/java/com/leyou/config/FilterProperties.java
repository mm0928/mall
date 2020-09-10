package com.leyou.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * 注入拦截器配置
 */
@ConfigurationProperties(prefix = "leyou.filter")
public class FilterProperties {

    /**
     * 拦截器
     */
    private List<String> allowPaths;

    public List<String> getAllowPaths() {
        System.out.println("allowPaths:" + allowPaths.toString());
        return allowPaths;
    }

    public void setAllowPaths(List<String> allowPaths) {
        this.allowPaths = allowPaths;
    }
}
