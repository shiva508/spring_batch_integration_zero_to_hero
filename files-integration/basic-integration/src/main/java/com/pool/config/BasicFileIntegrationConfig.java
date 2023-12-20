package com.pool.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.file.FileWritingMessageHandler;
import org.springframework.integration.file.filters.CompositeFileListFilter;
import org.springframework.integration.file.filters.LastModifiedFileListFilter;
import org.springframework.integration.file.filters.SimplePatternFileListFilter;
import org.springframework.integration.file.support.FileExistsMode;
import org.springframework.integration.file.transformer.FileToStringTransformer;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import java.io.File;

@Configuration
@Slf4j
@EnableIntegration
public class BasicFileIntegrationConfig {
    public static final String BASE_PATH="/home/shiva/Downloads/";

    @Autowired
    private ReadFileProcessor readFileProcessor;
    @Bean
    public MessageChannel fileInputMessageChannel(){
        return new DirectChannel();
    }

    @Bean
    public MessageChannel fileOutputMessageChannel(){
        return new DirectChannel();
    }
    @Bean
    @InboundChannelAdapter(value = "fileInputMessageChannel", poller = @Poller(fixedDelay = "1000"))
    public MessageSource<File> fileReadingMessageSource(){
        log.info("fileReadingMessageSource");
        FileReadingMessageSource readingMessageSource=new FileReadingMessageSource();
        CompositeFileListFilter<File> fileListFilter=new CompositeFileListFilter<>();
        readingMessageSource.setDirectory(new File(BASE_PATH+"input"));
        fileListFilter.addFilter(new SimplePatternFileListFilter("*.txt"));
        fileListFilter.addFilter(new LastModifiedFileListFilter());
        readingMessageSource.setFilter(fileListFilter);
        readingMessageSource.setAutoCreateDirectory(true);
        return readingMessageSource;
    }

    @Bean
    @ServiceActivator(inputChannel = "fileInputMessageChannel",outputChannel = "fileInputMessageChannel")
    public MessageHandler messageHandler(){
        log.info("messageHandler");
        FileWritingMessageHandler messageHandler=new FileWritingMessageHandler(new File(BASE_PATH+"output"));
        messageHandler.setFileExistsMode(FileExistsMode.REPLACE);
        messageHandler.setExpectReply(false);
        messageHandler.setDeleteSourceFiles(true);
        return messageHandler;
    }

    @Bean
    public FileToStringTransformer fileToStringTransformer(){
        return new FileToStringTransformer();
    }

    @Bean
    public IntegrationFlow fileIntegrationFlow(){
        return IntegrationFlow.from("fileOutputMessageChannel")
                              .transform(fileToStringTransformer())
                              .handle(readFileProcessor,"readMsg")
                              .get();
    }

}
