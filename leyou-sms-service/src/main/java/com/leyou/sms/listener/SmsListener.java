package com.leyou.sms.listener;

import com.aliyuncs.exceptions.ClientException;
import com.leyou.sms.config.SmsProperties;
import com.leyou.sms.utils.SmsUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 消息队列
 */
@Component
public class SmsListener {

    @Resource
    private SmsUtils smsUtils;

    @Resource
    private SmsProperties properties;

    /**
     * @param msg
     * @throws ClientException
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "leyou.sms.message", durable = "true"),
            exchange = @Exchange(value = "leyou.sms.exchange", ignoreDeclarationExceptions = "true", type = ExchangeTypes.TOPIC),
            key = {"sms.register"}
    ))

    /**
     *
     */
    public void listener(Map<String, String> msg) throws ClientException {
        if (CollectionUtils.isEmpty(msg)) {
            return;
        }
        // 解析消息
        String phone = msg.get("phone");
        String code = msg.get("code");
        //打印消息
        System.out.println("手机：" + phone + "验证码：" + code);

        if (StringUtils.isBlank(phone) || StringUtils.isBlank(code)) {
            // 放弃处理
            return;
        }
        smsUtils.sendSms(phone, code, this.properties.getSignName(), this.properties.getVerifyCodeTemplate());
    }
}
