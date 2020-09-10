package com.leyou.auth.service;

import com.leyou.auth.client.UserClient;
import com.leyou.auth.config.JwtProperties;
import com.leyou.auth.pojo.UserInfo;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.common.exception.LyException;
import com.leyou.user.pojo.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 *
 */
@Service
@EnableConfigurationProperties(JwtProperties.class)
public class AuthService {
    /**
     *
     */
    @Autowired
    private UserClient userClient;
    /**
     *
     */
    @Autowired
    private JwtProperties prop;

    /**
     * 日志
     */
    private static Logger logger = LoggerFactory.getLogger(AuthService.class);

    /**
     * 用户验证
     *
     * @param username
     * @param password
     * @return
     */
    public String accredit(String username, String password) {
        //调用用户中心的查询接口，校验用户信息
        System.out.println("调用用户中心的查询接口，校验用户信息,username+password" + username + password);
        //查询用户
        logger.info("开始client");
        try {
            // 调用微服务，执行查询
            User user = userClient.queryUser(username, password);
            // 如果查询结果为null，则直接返回null
            if (user == null) {
                return null;
            }
            //创建userInfo对象
            UserInfo userInfo = new UserInfo();
            userInfo.setId(user.getId());
            userInfo.setUsername(user.getUsername());
            //userinfo
            System.out.println(userInfo.toString());
            logger.info("结束client");
            return JwtUtils.generateToken(userInfo, this.prop.getPrivateKey(), this.prop.getExpire());
        } catch (Exception e) {
            logger.error("验证异常");
        }
        return null;
    }
}
