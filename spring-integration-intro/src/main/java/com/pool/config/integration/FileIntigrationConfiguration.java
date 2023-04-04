package com.pool.config.integration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.file.dsl.Files;
import org.springframework.messaging.MessageChannel;
import org.springframework.util.SystemPropertyUtils;

import java.io.File;

@Configuration
@ConditionalOnProperty(name = "integration.file",havingValue = "true")
public class FileIntigrationConfiguration {

    @Bean
    public MessageChannel fileInputMessageChannel(){
        return MessageChannels.direct().get();
    }
    @Bean
    public IntegrationFlow fileInputIntegrationFlow(){
        var directory = new File(SystemPropertyUtils.resolvePlaceholders("${HOME}/Desktop/input"));
        return IntegrationFlow.from(Files.inboundAdapter(directory).autoCreateDirectory(true),polling -> polling.poller(pf -> pf.fixedRate(1000)))
                .channel(fileInputMessageChannel())
                .get();
    }

    @Bean
    public IntegrationFlow fileOutputIntegrationFlow(){
        var directory = new File(SystemPropertyUtils.resolvePlaceholders("${HOME}/Desktop/output"));
        return IntegrationFlow.from(fileInputMessageChannel())
                .handle(Files.outboundAdapter(directory).autoCreateDirectory(true).deleteSourceFiles(true))
                .get();
    }
}
