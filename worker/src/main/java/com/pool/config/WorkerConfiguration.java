package com.pool.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pool.records.YearReport;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.step.item.ChunkProcessor;
import org.springframework.batch.core.step.item.SimpleChunkProcessor;
import org.springframework.batch.integration.chunk.ChunkProcessorChunkHandler;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.amqp.dsl.Amqp;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.lang.NonNull;

@Configuration
public class WorkerConfiguration {

    private final ObjectMapper objectMapper;

    public WorkerConfiguration(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Bean
    public DirectChannel requests(){
        return MessageChannels.direct().get();
    }

    @Bean
    public DirectChannel replies(){
        return MessageChannels.direct().get();
    }

    @Bean
    public IntegrationFlow messageIn(ConnectionFactory connectionFactory){
        return IntegrationFlow.from(Amqp.inboundAdapter(connectionFactory,"requests"))
                              .channel(requests())
                              .get();
    }

    @Bean
    public IntegrationFlow outgoingReplies(AmqpTemplate amqpTemplate){
        return IntegrationFlow.from("replies")
                .handle(Amqp.outboundAdapter(amqpTemplate).routingKey("replies"))
                .get();
    }

    public YearReport stringToObjectMapper(String input){
        try {
            return objectMapper.readValue(input,YearReport.class);
        }catch (JsonProcessingException exception){
            throw new IllegalArgumentException("Something went wrong");
        }

    }

    @Bean
    @ServiceActivator(inputChannel = "requests",outputChannel = "replies",sendTimeout = "10000")
    public ChunkProcessorChunkHandler<String> chunkProcessorChunkHandler(){
        ChunkProcessor<String> chunkProcessor=new SimpleChunkProcessor<>(rowDataToObjectProcessor(),itemWriter());
        ChunkProcessorChunkHandler<String> chunkProcessorChunkHandler = new ChunkProcessorChunkHandler<>();
        chunkProcessorChunkHandler.setChunkProcessor(chunkProcessor);
        return chunkProcessorChunkHandler;
    }

    @Bean
    public ItemProcessor<String, YearReport> rowDataToObjectProcessor(){
        return message -> {
            //System.out.println(">> processing YearReport JSON: " + message);
            Thread.sleep(5);
           return stringToObjectMapper(message);
        };
    }

    @Bean
    public ItemWriter<YearReport> itemWriter() {
        return chunk -> chunk.getItems().forEach(this::taskTakesTimeToComplete);
    }

    public void taskTakesTimeToComplete(YearReport yearReport){
        System.out.println("=================================");
        System.out.println(yearReport.year()+":"+yearReport.breakout());
    }
}
