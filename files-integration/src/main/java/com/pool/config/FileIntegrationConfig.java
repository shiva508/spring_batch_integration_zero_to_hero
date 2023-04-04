package com.pool.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.core.GenericHandler;
import org.springframework.integration.core.GenericTransformer;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.event.inbound.ApplicationEventListeningMessageProducer;
import org.springframework.integration.file.dsl.Files;
import org.springframework.integration.ftp.server.ApacheMinaFtpEvent;
import org.springframework.integration.ftp.server.ApacheMinaFtplet;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;

import java.io.File;

//@Configuration
public class FileIntegrationConfig {

    @Bean
    public ApacheMinaFtplet apacheMinaFtplet(){
        return  new ApacheMinaFtplet();
    }
    @Bean
    public MessageChannel messageChannel(){
        return MessageChannels.direct().get();
    }
    @Bean
    public IntegrationFlow integrationFlow(){
        return IntegrationFlow.from(messageChannel())
                .handle(new GenericHandler<ApacheMinaFtpEvent>() {
                    @Override
                    public Object handle(ApacheMinaFtpEvent payload, MessageHeaders headers) {
                        //System.out.println("Something new found :"+payload.getSession());
                        return null;
                    }
                }).get();
    }

    @Bean
    public ApplicationEventListeningMessageProducer applicationEventListeningMessageProducer(){
        var producer=new ApplicationEventListeningMessageProducer();
        producer.setEventTypes(ApacheMinaFtpEvent.class);
        producer.setOutputChannel(messageChannel());
        return producer;
    }

}
