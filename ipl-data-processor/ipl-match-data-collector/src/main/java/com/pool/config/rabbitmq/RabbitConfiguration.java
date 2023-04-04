package com.pool.config.rabbitmq;

import com.pool.config.props.TransmitterConfig;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class RabbitConfiguration {
    private final TransmitterConfig transmitterConfig;

    public RabbitConfiguration(TransmitterConfig transmitterConfig) {
        this.transmitterConfig = transmitterConfig;
    }

    @Bean
    public Queue requestQueue() {
        return new Queue(transmitterConfig.getOutbound(), false);
    }

    @Bean
    public Queue repliesQueue() {
        return new Queue(transmitterConfig.getInbound(), false);
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(transmitterConfig.getExchange());
    }

    @Bean
    public Binding repliesBinding(TopicExchange exchange) {
        return BindingBuilder.bind(repliesQueue()).to(exchange).with(transmitterConfig.getInbound());
    }

    @Bean
    public Binding requestBinding(TopicExchange exchange) {
        return BindingBuilder.bind(requestQueue()).to(exchange).with(transmitterConfig.getOutbound());
    }

}
