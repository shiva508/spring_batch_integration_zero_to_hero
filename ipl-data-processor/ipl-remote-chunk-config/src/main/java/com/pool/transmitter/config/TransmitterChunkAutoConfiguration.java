package com.pool.transmitter.config;

import com.pool.transmitter.annotation.TransmitterChunkStep;
import com.pool.transmitter.annotation.TransmitterInboundChunkChannel;
import com.pool.transmitter.annotation.TransmitterItemWriter;
import com.pool.transmitter.annotation.TransmitterOutboundChunkChannel;
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
@ConditionalOnProperty(value = "batch.chunk.transmitter",havingValue = "true")
public class TransmitterChunkAutoConfiguration {

    private final static String MESSAGING_TEMPLATE_BEAN_NAME = "transmitterChunkMessagingTemplate";
    //SETTING INBOUND, OUTBOUND CHANNELS

    //Configure outbound flow (requests going to receiver)
    @Bean
    @TransmitterOutboundChunkChannel
    public DirectChannel transmitterRequestsMessageChannel(){
        return MessageChannels.direct().get();
    }

    //Configure inbound flow (replies coming from receiver)
    @Bean
    @TransmitterInboundChunkChannel
    public QueueChannel transmitterRepliesMessageChannel(){
        return MessageChannels.queue().get();
    }


    @Bean
    @ConditionalOnMissingBean
    @Qualifier(MESSAGING_TEMPLATE_BEAN_NAME)
    public MessagingTemplate transmitterChunkMessagingTemplate(){
        MessagingTemplate messagingTemplate = new MessagingTemplate();
        messagingTemplate.setDefaultChannel(transmitterRequestsMessageChannel());
        messagingTemplate.setReceiveTimeout(2000);
        return messagingTemplate;
    }
    @Bean
    @TransmitterItemWriter
    public ChunkMessageChannelItemWriter<?> chunkMessageChannelItemWriter(@Qualifier(MESSAGING_TEMPLATE_BEAN_NAME) MessagingTemplate txChunkMessagingTemplate){
        ChunkMessageChannelItemWriter<?>  messageChannelItemWriter=new ChunkMessageChannelItemWriter<>();
        messageChannelItemWriter.setMessagingOperations(txChunkMessagingTemplate);
        messageChannelItemWriter.setReplyChannel(transmitterRepliesMessageChannel());
        return messageChannelItemWriter;
    }

    @Bean
    @ConditionalOnMissingBean
   public RemoteChunkHandlerFactoryBean<Object> transmitterRemoteChunkHandler(ChunkMessageChannelItemWriter<Object> chunkMessageChannelItemWriterProxy,
                                                                              @TransmitterChunkStep TaskletStep taskletStep){
        var remoteChunkHandlerFactoryBean = new RemoteChunkHandlerFactoryBean<>();
        remoteChunkHandlerFactoryBean.setChunkWriter(chunkMessageChannelItemWriterProxy);
        remoteChunkHandlerFactoryBean.setStep(taskletStep);
        return remoteChunkHandlerFactoryBean;
    }

}
