package com.pool.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.core.GenericHandler;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.event.inbound.ApplicationEventListeningMessageProducer;
import org.springframework.integration.ftp.server.ApacheMinaFtpEvent;
import org.springframework.integration.ftp.server.ApacheMinaFtplet;
import org.springframework.messaging.MessageChannel;

@Configuration
@Slf4j

public class IntegrationConfiguration {

    @Bean
    public ApacheMinaFtplet apacheMinaFtplet(){
        return new ApacheMinaFtplet();
    }
    @Bean
    public MessageChannel messageChannel(){
        return MessageChannels.direct().get();
    }

    @Bean
    public IntegrationFlow  integrationFlow(){
        return IntegrationFlow.from(messageChannel())
                .handle((GenericHandler<ApacheMinaFtpEvent>) (payload, headers) -> {
                    log.info("new event: " + payload.getClass().getName() + ':' + payload.getSession());
                    return null;
                }).get();
    }

    @Bean
    public ApplicationEventListeningMessageProducer messageProducer(){
        var producer=new ApplicationEventListeningMessageProducer();
        producer.setEventTypes(ApacheMinaFtpEvent.class);
        producer.setOutputChannel(messageChannel());
        return producer;
    }
}
