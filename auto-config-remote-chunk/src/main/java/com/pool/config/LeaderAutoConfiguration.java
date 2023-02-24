package com.pool.config;

import com.pool.annotation.ChunkingItemWriter;
import com.pool.annotation.ChunkingStep;
import com.pool.annotation.InboundChunkChannel;
import com.pool.annotation.OutboundChunkChannel;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.integration.chunk.ChunkMessageChannelItemWriter;
import org.springframework.batch.integration.chunk.RemoteChunkHandlerFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.messaging.MessageChannel;

@Configuration
public class LeaderAutoConfiguration {
    public LeaderAutoConfiguration() {
    }
    @Bean
    @ConditionalOnMissingBean
    @Qualifier("remoteChunkMessagingTemplate")
    public MessagingTemplate remoteChunkMessagingTemplate(@OutboundChunkChannel MessageChannel channel){
        MessagingTemplate messagingTemplate=new MessagingTemplate();
        messagingTemplate.receive(channel);
        messagingTemplate.setReceiveTimeout(1000);
        return messagingTemplate;

    }

    @Bean
    @ConditionalOnMissingBean
    public RemoteChunkHandlerFactoryBean<Object> remoteChunkHandler(
            ChunkMessageChannelItemWriter<Object> chunkMessageChannelItemWriterProxy,
            @ChunkingStep TaskletStep step){
        RemoteChunkHandlerFactoryBean<Object> objectRemoteChunkHandlerFactoryBean = new RemoteChunkHandlerFactoryBean<>();
        objectRemoteChunkHandlerFactoryBean.setChunkWriter(chunkMessageChannelItemWriterProxy);
        objectRemoteChunkHandlerFactoryBean.setStep(step);
        return objectRemoteChunkHandlerFactoryBean;
    }

    @Bean
    @ChunkingItemWriter
    @StepScope
    @ConditionalOnMissingBean
    public ChunkMessageChannelItemWriter<?> chunkMessageChannelItemWriter(@Qualifier("remoteChunkMessagingTemplate")MessagingTemplate messagingTemplate ){
        ChunkMessageChannelItemWriter<Object> chunkMessageChannelItemWriter = new ChunkMessageChannelItemWriter<>();
        chunkMessageChannelItemWriter.setMessagingOperations(messagingTemplate);
        chunkMessageChannelItemWriter.setReplyChannel(remoteChunkRepliesMessageChannel());
        return chunkMessageChannelItemWriter;
    }

    @Bean
    @ConditionalOnMissingBean
    @OutboundChunkChannel
    public DirectChannel remoteChunkRequestMessageChannel(){
        return MessageChannels.direct().get();
    }
    @Bean
    @ConditionalOnMissingBean
    @InboundChunkChannel
    public QueueChannel remoteChunkRepliesMessageChannel(){
        return MessageChannels.queue().get();
    }
}
