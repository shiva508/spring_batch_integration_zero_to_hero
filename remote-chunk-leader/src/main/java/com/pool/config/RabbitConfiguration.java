package com.pool.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.TopicExchange;
import  org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class RabbitConfiguration {

    @Bean
    public Queue requestQueue() {
        return new Queue("requests", false);
    }

    @Bean
    public Queue repliesQueue() {
        return new Queue("replies", false);
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange("remote-chunking-exchange");
    }

    @Bean
    public Binding repliesBinding(TopicExchange exchange) {
        return BindingBuilder.bind(repliesQueue()).to(exchange).with("replies");
    }

    @Bean
    public Binding requestBinding(TopicExchange exchange) {
        return BindingBuilder.bind(requestQueue()).to(exchange).with("requests");
    }

}
