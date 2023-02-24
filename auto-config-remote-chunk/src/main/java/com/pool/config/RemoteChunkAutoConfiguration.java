package com.pool.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pool.annotation.*;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.integration.chunk.ChunkMessageChannelItemWriter;
import org.springframework.batch.integration.chunk.ChunkRequest;
import org.springframework.batch.integration.chunk.ChunkResponse;
import org.springframework.batch.integration.chunk.RemoteChunkHandlerFactoryBean;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;

@Configuration
public class RemoteChunkAutoConfiguration {


    private final ObjectMapper objectMapper;

    public RemoteChunkAutoConfiguration(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Bean
    @ChunkingStep
    public TaskletStep taskletStep(JobRepository jobRepository,
                                   PlatformTransactionManager transactionManager,
                                   @ChunkingItemWriter ItemWriter<String> itemWriter){
        ListItemReader<Customer> customerReaders = new ListItemReader<>(List.of(new Customer("Shiva")));
        return new StepBuilder("step",jobRepository)
                .<Customer,String>chunk(100,transactionManager)
                .reader(customerReaders)
                .processor(this::jsonFormConverter)
                .writer(itemWriter)
                .build();
    }

    public String jsonFormConverter(Customer customer) {
        try {
            return objectMapper.writeValueAsString(customer);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
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

    public RemoteChunkHandlerFactoryBean<Object> remoteChunkHandler(
            ChunkMessageChannelItemWriter<Object> chunkMessageChannelItemWriterProxy,
            @ChunkingStep TaskletStep step
    ){
     return null;
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


    @Bean
    public IntegrationFlow chunkIntegrationFlow(@InboundChunkChannel MessageChannel inbound,
                                                @OutboundChunkChannel MessageChannel outbound){
        return IntegrationFlow.from(outbound)
                .handle(message -> {
                    if(message.getPayload() instanceof ChunkRequest<?> chunkRequest){
                        ChunkResponse chunkResponse = new ChunkResponse(chunkRequest.getSequence(),
                                                                        chunkRequest.getJobId(),
                                                                        chunkRequest.getStepContribution());
                        inbound.send(MessageBuilder.withPayload(chunkResponse).build());
                    }
                }).get();
    }

    @Bean
    public Job job(JobRepository jobRepository, Step step){
        return new JobBuilder("Job",jobRepository)
                .start(step)
                .build();
    }
}
