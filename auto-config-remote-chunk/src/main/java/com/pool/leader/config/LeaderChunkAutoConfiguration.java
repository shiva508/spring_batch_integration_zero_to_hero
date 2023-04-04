package com.pool.leader.config;

import com.pool.leader.annotation.LeaderChunkStep;
import com.pool.leader.annotation.LeaderInboundChunkChannel;
import com.pool.leader.annotation.LeaderItemWriter;
import com.pool.leader.annotation.LeaderOutboundChunkChannel;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.integration.chunk.ChunkMessageChannelItemWriter;
import org.springframework.batch.integration.chunk.RemoteChunkHandlerFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.integration.dsl.MessageChannels;

@Configuration
@ConditionalOnProperty(value = "bootiful.batch.chunk.leader", havingValue = "true")
public class LeaderChunkAutoConfiguration {

    private final static String MESSAGING_TEMPLATE_BEAN_NAME = "leaderChunkMessagingTemplate";

    @Bean
    @LeaderOutboundChunkChannel
    public DirectChannel leaderRequestsMessageChannel(){
        return MessageChannels.direct().get();
    }

    @Bean
    @LeaderInboundChunkChannel
    public QueueChannel leaderRepliesMessageChannel() {
        return MessageChannels.queue().get();
    }

    @Bean
    @ConditionalOnMissingBean
    @Qualifier(MESSAGING_TEMPLATE_BEAN_NAME)
    public MessagingTemplate leaderChunkMessagingTemplate() {
        var template = new MessagingTemplate();
        template.setDefaultChannel(leaderRequestsMessageChannel());
        template.setReceiveTimeout(2000);
        return template;
    }

    @Bean
    @ConditionalOnMissingBean
    public RemoteChunkHandlerFactoryBean<Object> leaderChunkHandler(
            ChunkMessageChannelItemWriter<Object> chunkMessageChannelItemWriterProxy,
            @LeaderChunkStep TaskletStep step) {
        var remoteChunkHandlerFactoryBean = new RemoteChunkHandlerFactoryBean<>();
        remoteChunkHandlerFactoryBean.setChunkWriter(chunkMessageChannelItemWriterProxy);
        remoteChunkHandlerFactoryBean.setStep(step);
        return remoteChunkHandlerFactoryBean;
    }

    @Bean
    @LeaderItemWriter
        // @StepScope
    public ChunkMessageChannelItemWriter<?> leaderChunkMessageChannelItemWriter(
            @Qualifier(MESSAGING_TEMPLATE_BEAN_NAME) MessagingTemplate template) {
        var chunkMessageChannelItemWriter = new ChunkMessageChannelItemWriter<>();
        chunkMessageChannelItemWriter.setMessagingOperations(template);
        chunkMessageChannelItemWriter.setReplyChannel(leaderRepliesMessageChannel());
        return chunkMessageChannelItemWriter;
    }
}
