package com.pool.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;

@Configuration
public class BasicFileIntegrationConfig {

    @Bean
    public MessageChannel txFileMessageChannel(){
        return new DirectChannel();
    }

}
