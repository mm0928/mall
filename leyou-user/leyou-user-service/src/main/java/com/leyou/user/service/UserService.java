package com.leyou.user.service;

import com.leyou.common.utils.NumberUtils;
import com.leyou.user.mapper.UserMapper;
import com.leyou.user.pojo.User;
import com.leyou.user.utils.CodecUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private AmqpTemplate amqpTemplate;

    static final String KEY_PREFIX = "leyou:user:code:";

    static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public UserService(AmqpTemplate amqpTemplate) {
        this.amqpTemplate = amqpTemplate;
    }

    /**
     * 发送短信
     *
     * @param phone
     * @return
     */
    public Boolean sendVerifyCode(String phone) {
        // 生成验证码
        String code = NumberUtils.generateCode(6);
        try {
            // 发送短信
            Map<String, String> msg = new HashMap<>();
            msg.put("phone", phone);
            msg.put("code", code);
            //调用阿里云，发送短信
            this.amqpTemplate.convertAndSend("leyou.sms.exchange", "sms.register", msg);
            // 将code存入redis
            this.redisTemplate.opsForValue().set(KEY_PREFIX + phone, code, 5, TimeUnit.MINUTES);
            System.out.println("code = " + code);
            return true;
        } catch (Exception e) {
            logger.error("发送短信失败。phone：{}， code：{},{}", phone, code, e);
            return false;
        }
    }

    /**
     * @param data
     * @param type
     * @return
     */
    public Boolean checkData(String data, Integer type) {
        User record = new User();
        switch (type) {
            case 1:
                record.setUsername(data);
                break;
            case 2:
                record.setPhone(data);
                break;
            default:
                return null;
        }
        return this.userMapper.selectCount(record) == 0;
    }

    /**
     * @param user
     * @param code
     * @return
     */
    public Boolean register(User user, String code) {
        //校验短信验证码
        String cacheCode = this.redisTemplate.opsForValue().get(KEY_PREFIX + user.getPhone());
        if (!StringUtils.equals(code, cacheCode)) {
            return false;
        }
        //生成盐
        String salt = CodecUtils.generateSalt();
        user.setSalt(salt);

        //对密码加密
        String passw = CodecUtils.md5Hex(user.getPassword(), salt);
        user.setPassword(passw);

        //qiang'zh强制设置不能指定的参数为null
        user.setId(null);
        user.setCreated(new Date());
        //添加到数据库
        boolean b = this.userMapper.insertSelective(user) == 1;
        if (b) {
            //注册成功
            this.redisTemplate.delete(KEY_PREFIX + user.getPhone());
        }
        return b;
    }

    /**
     * @param username
     * @param password
     * @return
     */
    public User queryUser(String username, String password) {

        //查询
        User record = new User();
        record.setUsername(username);
        User user = this.userMapper.selectOne(record);
        //若明湖为null
        if (user == null) {
            return null;
        }
        //校验用户名
        if (!user.getPassword().equals(CodecUtils.md5Hex(password, user.getSalt()))) {
            return null;
        }
        //用户名密码都正确
        return user;
    }
}