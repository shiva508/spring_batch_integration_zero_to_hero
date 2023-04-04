package com.pool.config.integration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.core.GenericHandler;
import org.springframework.integration.core.GenericTransformer;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;

import java.time.Instant;

@Configuration
@ConditionalOnProperty(name = "integration.basic",havingValue = "true")
public class BasicIntegrationWithMessageChannelConfiguration {
    @Bean(name = "transmitterChannelBasic")
    public MessageChannel transmitterChannelBasic(){
        return MessageChannels.direct().get();
    }

    @Bean
    public IntegrationFlow transmitterIntegrationFlow(){
        return IntegrationFlow.from((MessageSource<String>) () -> MessageBuilder.withPayload("Sending data "+ Instant.now()+" !")
                .build(), poller ->poller.poller(pf -> pf.fixedRate(100)) )
                .transform((GenericTransformer<String, String>) source -> source.toUpperCase() +" with length="+source.length())
                .channel(transmitterChannelBasic())
                .get();
    }

    @Bean
    public IntegrationFlow receiverIntegrationFlow(){
        return IntegrationFlow.from(transmitterChannelBasic())
                .handle((GenericHandler<String>) (payload, headers) -> {
                    System.out.println("Received Message: "+payload);
                    return null;
                }).get();
    }
}
