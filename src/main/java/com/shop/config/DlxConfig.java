package com.shop.config;

import org.springframework.amqp.core.*;

import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DlxConfig {
    @Bean
    public Queue simpleQueue(){
        return QueueBuilder.durable("simple")
                .withArgument("x-dead-letter-exchange", "dlxExchange")
                .withArgument("x-dead-letter-routing-key", "dlxRoutingKey")
                .withArgument("x-message-ttl", 10000)
                .build();

    }
    @Bean
    public Queue dlxQueue(){
        return QueueBuilder.durable("dlxQueue").build();
    }
    @Bean
    public DirectExchange dlxExchange(){
        return new DirectExchange("dlxExchange");
    }
    @Bean
    public DirectExchange simpleExchange(){
        return new DirectExchange("simpleExchange");
    }
    @Bean
    public Binding simpleBinding(Queue simpleQueue, DirectExchange simpleExchange){
        return  BindingBuilder.bind(simpleQueue).to(simpleExchange).with("simpleRoutingKey");
    }
    @Bean
    public Binding dlxBinding(Queue dlxQueue, DirectExchange dlxExchange){
        return BindingBuilder.bind(dlxQueue).to(dlxExchange).with("dlxRoutingKey");
    }


}
