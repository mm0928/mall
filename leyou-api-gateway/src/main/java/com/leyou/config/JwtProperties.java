package com.leyou.config;

import com.leyou.auth.utils.RsaUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.security.PublicKey;

/**
 * 注入jwt
 */
@ConfigurationProperties(prefix = "leyou.jwt")
public class JwtProperties {

    /**
     * 公钥
     */
    private String pubKeyPath;
    /**
     * 公钥
     */
    private PublicKey publicKey;
    /**
     * cookie
     */
    private String cookieName;

    private static final Logger logger = LoggerFactory.getLogger(JwtProperties.class);

    /**
     * @PostConstruct注解的方法将会在依赖注入完成后被自动调用,方法会在服务器加载Servlet的时候运行， 并且只会被服务器执行一次。
     */
    @PostConstruct
    public void init() {
        try {
            // 获取公钥和私钥
            System.out.println(pubKeyPath.toString());
            System.out.println(publicKey.toString());
            this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        } catch (Exception e) {
            logger.error("初始化公钥失败！", e);
            throw new RuntimeException();
        }
    }

    public String getPubKeyPath() {
        return pubKeyPath;
    }

    public void setPubKeyPath(String pubKeyPath) {
        this.pubKeyPath = pubKeyPath;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public String getCookieName() {
        return cookieName;
    }

    public void setCookieName(String cookieName) {
        this.cookieName = cookieName;
    }
}