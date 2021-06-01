package com.xjx.mydns.rabbitmq;

import com.alibaba.fastjson.JSONObject;
import com.xjx.mydns.entity.Rule;
import com.xjx.mydns.transmit.entity.RuleVo;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 消息发送者
 */
@Service
public class MessageSender {
    @Autowired
    RabbitTemplate rabbitTemplate;

    // 发送第二次的异步清理缓存指令
    public boolean sendMessage(RuleVo rule) throws Exception{
        String json = JSONObject.toJSONString(rule);
        rabbitTemplate.convertAndSend(MQconfig.REDIS_FLUSH_EXCHANGE,MQconfig.REDIS_FLUSH_ROUTEKEY,json);
        return true;
    }
}
