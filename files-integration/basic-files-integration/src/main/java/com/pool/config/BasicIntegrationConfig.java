package com.pool.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.GenericHandler;
import org.springframework.integration.core.GenericTransformer;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import java.time.Instant;

@Configuration
@ConditionalOnProperty(value = "ftp.app-one",havingValue = "true")
public class BasicIntegrationConfig {

    @Bean
    public MessageChannel messageChannel(){
        return new DirectChannel();
    }

    @Bean
    public MessageChannel rxMessageChannel(){
        return new DirectChannel();
    }

    @Bean
    public IntegrationFlow inputIntegrationFlow(){
        return IntegrationFlow.from((MessageSource<String>)()->MessageBuilder.withPayload("My Coordinate "+ Instant.now()+" !").build(),poller->poller.poller(pf ->pf.fixedRate(1000)))
                              .transform((GenericTransformer<String, String>) String::toUpperCase)
                              .channel(messageChannel())
                              .get();
    }

    @Bean
    public IntegrationFlow middleIntegrationFlow(){
       return IntegrationFlow.from(messageChannel())
                             .handle((GenericHandler<String>)(payload, header)-> payload).channel(rxMessageChannel())
                             .get();
    }

    @Bean
    public IntegrationFlow finalIntegrationFlow(){
        return IntegrationFlow.from(rxMessageChannel())
                                .handle((GenericHandler<String>)(payload, header)->{
                                        System.out.println("payload="+payload+",header="+header);
                                        return null;
                                 }).get();
    }

}
