package com.pool.worker.config;

import com.pool.worker.annotation.WorkerInboundChunkChannel;
import com.pool.worker.annotation.WorkerItemProcessor;
import com.pool.worker.annotation.WorkerItemWriter;
import com.pool.worker.annotation.WorkerOutboundChunkChannel;
import org.springframework.batch.core.step.item.SimpleChunkProcessor;
import org.springframework.batch.integration.chunk.ChunkProcessorChunkHandler;
import org.springframework.batch.integration.chunk.ChunkRequest;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.Assert;

@Configuration
@ConditionalOnProperty(value = "bootiful.batch.chunk.worker", havingValue = "true")
public class WorkerChunkAutoConfiguration {
    @Bean
    @WorkerInboundChunkChannel
    public DirectChannel workerRequestsMessageChannel() {
        return MessageChannels.direct().get();
    }
    @Bean
    @WorkerOutboundChunkChannel
    public DirectChannel workerRepliesMessageChannel() {
        return MessageChannels.direct().get();
    }
    @Bean
    @ConditionalOnMissingBean
    @SuppressWarnings("unchecked")
    public ChunkProcessorChunkHandler<?> workerChunkProcessorChunkHandler(
            // todo make this optional
            // @WorkerChunkingItemProcessor ObjectProvider<ItemProcessor<Object, Object>>
            // processor,
            @WorkerItemProcessor ItemProcessor<?, ?> processor, @WorkerItemWriter ItemWriter<?> writer) {
        var chunkProcessorChunkHandler = new ChunkProcessorChunkHandler<>();
        chunkProcessorChunkHandler.setChunkProcessor(new SimpleChunkProcessor(processor, writer));
        return chunkProcessorChunkHandler;
    }
    @Bean
    @SuppressWarnings("unchecked")
   public IntegrationFlow chunkProcessorChunkHandlerIntegrationFlow(
            ChunkProcessorChunkHandler<Object> chunkProcessorChunkHandler) {
        return IntegrationFlow//
                .from(workerRequestsMessageChannel())//
                .handle(message -> {
                    try {
                        var payload = message.getPayload();
                        if (payload instanceof ChunkRequest<?> cr) {
                            var chunkResponse = chunkProcessorChunkHandler.handleChunk((ChunkRequest<Object>) cr);
                            workerRepliesMessageChannel().send(MessageBuilder.withPayload(chunkResponse).build());
                        }
                        Assert.state(payload instanceof ChunkRequest<?>,
                                "the payload must be an instance of ChunkRequest!");
                    } //
                    catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })//
                .get();
    }
}
