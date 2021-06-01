package com.xjx.mydns.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MQconfig {
    //队列
    public static final String  REDIS_FLUSH_QUEUE = "redis.flush.queue";
    //交换机
    public static final String  REDIS_FLUSH_EXCHANGE = "redis.flush.exchange";
    //路由键
    public static final String  REDIS_FLUSH_ROUTEKEY = "redis.flush.routingKey";

    @Bean
    public DirectExchange getdirectExchange(){
        return new DirectExchange(REDIS_FLUSH_EXCHANGE);
    }
    @Bean
    public Queue getdirectQueue(){
        return new Queue(REDIS_FLUSH_QUEUE);
    }
    @Bean
    public Binding bindQueueAndExchange(){
        return BindingBuilder.bind(getdirectQueue()).to(getdirectExchange()).with(REDIS_FLUSH_ROUTEKEY);
    }
}
