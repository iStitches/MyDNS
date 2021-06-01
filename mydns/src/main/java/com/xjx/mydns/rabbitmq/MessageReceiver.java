package com.xjx.mydns.rabbitmq;

import com.xjx.mydns.constant.CommonEnum;
import com.xjx.mydns.constant.Constants;
import com.xjx.mydns.entity.Rule;
import com.xjx.mydns.exception.MyException;
import com.xjx.mydns.transmit.entity.RuleVo;
import com.xjx.mydns.utils.CommonUtils;
import com.xjx.mydns.utils.RedisUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageReceiver {
    @Autowired
    CommonUtils commonUtils;
    @Autowired
    RedisUtils redisUtils;

    @RabbitListener(queues = {MQconfig.REDIS_FLUSH_QUEUE})
    public void receiveMsg(String msg){
        RuleVo ruleVo = commonUtils.convertJsonToObject(msg, RuleVo.class);
        if(ruleVo == null)
            throw new MyException(CommonEnum.MESSAGE_MQ_RECEIVE_ERROR);
        // 删除缓存
        redisUtils.zsetRemove(Constants.DNS_RULE_VO,ruleVo);
        redisUtils.delList(ruleVo.getRuleName());
    }
}
