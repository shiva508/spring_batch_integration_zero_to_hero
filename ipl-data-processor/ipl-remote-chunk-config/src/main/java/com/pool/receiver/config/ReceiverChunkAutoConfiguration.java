package com.pool.receiver.config;

import com.pool.receiver.annotation.ReceiverInboundChunkChannel;
import com.pool.receiver.annotation.ReceiverItemProcessor;
import com.pool.receiver.annotation.ReceiverItemWriter;
import com.pool.receiver.annotation.ReceiverOutboundChunkChannel;
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
@ConditionalOnProperty(value = "batch.chunk.receiver",havingValue = "true")
public class ReceiverChunkAutoConfiguration {

    @Bean
    @ReceiverInboundChunkChannel
    public DirectChannel receiverRequestsMessageChannel(){
        return MessageChannels.direct().get();
    }

    @Bean
    @ReceiverOutboundChunkChannel
    public DirectChannel receiverRepliesMessageChannel(){
        return MessageChannels.direct().get();
    }

    @Bean
    @ConditionalOnMissingBean
    public ChunkProcessorChunkHandler<?> receiverChunkProcessorChunkHandler(
            @ReceiverItemProcessor ItemProcessor<?,?> itemProcessor,
            @ReceiverItemWriter ItemWriter<?> itemWriter){
        var chunkProcessorChunkHandler = new ChunkProcessorChunkHandler<>();
        chunkProcessorChunkHandler.setChunkProcessor(new SimpleChunkProcessor(itemProcessor, itemWriter));
        return chunkProcessorChunkHandler;
    }

    @Bean
    public IntegrationFlow receiverIntegrationFlow(ChunkProcessorChunkHandler<Object> chunkProcessorChunkHandler){
        return IntegrationFlow.from(receiverRequestsMessageChannel())
                .handle(message -> {
                    try {
                        var payload = message.getPayload();
                        if (payload instanceof ChunkRequest<?> cr) {
                            var chunkResponse = chunkProcessorChunkHandler.handleChunk((ChunkRequest<Object>) cr);
                            receiverRepliesMessageChannel().send(MessageBuilder.withPayload(chunkResponse).build());
                        }
                        Assert.state(payload instanceof ChunkRequest<?>,"the payload must be an instance of ChunkRequest!");
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }).get();
    }
}
