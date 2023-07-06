package com.pool.config.pubsub;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.PublishSubscribeChannel;

@Configuration
public class PubSubIntegrationConfig {
    @Bean
    public PublishSubscribeChannel userPublishSubscribeChannel(){
        return new PublishSubscribeChannel();
    }
}
