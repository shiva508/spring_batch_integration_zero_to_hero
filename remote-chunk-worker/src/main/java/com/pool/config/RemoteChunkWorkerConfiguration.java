package com.pool.config;

import com.pool.worker.annotation.WorkerInboundChunkChannel;
import com.pool.worker.annotation.WorkerItemProcessor;
import com.pool.worker.annotation.WorkerItemWriter;
import com.pool.worker.annotation.WorkerOutboundChunkChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.amqp.dsl.Amqp;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.messaging.MessageChannel;

import java.util.List;

@Configuration
@Slf4j
public class RemoteChunkWorkerConfiguration {

    @Bean
    @WorkerItemProcessor
    public ItemProcessor<Object, Object> itemProcessor() {
        return item -> item;
    }

    @Bean
    @WorkerItemWriter
    public ItemWriter<Object> itemWriter() {
        return chunk -> {
            //
            log.info("doing the long-running writing thing");
            List<?> items = chunk.getItems();
            for (var i : items)
                log.info("i={}", i + "");
        };
    }

    @Bean
    public IntegrationFlow inboundAmqpIntegrationFlow(@WorkerInboundChunkChannel MessageChannel workerRequestsMessageChannel,
                                               ConnectionFactory connectionFactory) {
        return IntegrationFlow//
                .from(Amqp.inboundAdapter(connectionFactory, "requests"))//
                .channel(workerRequestsMessageChannel)//
                .get();
    }

    @Bean
    public IntegrationFlow outboundAmqpIntegrationFlow(@WorkerOutboundChunkChannel MessageChannel workerRepliesMessageChannel,
                                                       AmqpTemplate template) {
        return IntegrationFlow //
                .from(workerRepliesMessageChannel)//
                .handle(Amqp.outboundAdapter(template).routingKey("replies"))//
                .get();
    }


}
