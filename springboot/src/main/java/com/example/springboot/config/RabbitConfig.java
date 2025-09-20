package com.example.springboot.config;

import com.example.springboot.common.constant.RabbitConstant;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    @Bean
    public Queue makeQueue(){
        return new Queue(RabbitConstant.SECKILL_ORDER_QUEUE,true,false,false);
    }
    @Bean
    public DirectExchange makeDirectExchange(){
        return new DirectExchange(RabbitConstant.SECKILL_ORDER_EXCHANGE,true,false);
    }
    @Bean
    public Binding bindDirect(){
        return BindingBuilder.bind(makeQueue()).to(makeDirectExchange()).with(RabbitConstant.SECKILL_ORDER_ROUTING_KEY);
    }

}
