package com.pool.config;

import com.pool.config.props.TransmitterConfig;
import com.pool.receiver.annotation.ReceiverInboundChunkChannel;
import com.pool.receiver.annotation.ReceiverItemProcessor;
import com.pool.receiver.annotation.ReceiverItemWriter;
import com.pool.receiver.annotation.ReceiverOutboundChunkChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.amqp.dsl.Amqp;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.messaging.MessageChannel;
import java.util.List;

@Configuration
@Slf4j
@ConditionalOnProperty(value = "batch.chunk.receiver",havingValue = "true")
public class TweetReceiverRemoteChunkConfiguration {

    private final TransmitterConfig transmitterConfig;

    public TweetReceiverRemoteChunkConfiguration(TransmitterConfig transmitterConfig) {
        this.transmitterConfig = transmitterConfig;
    }

    @Bean
    public IntegrationFlow receiverInboundintegrationFlow(ConnectionFactory connectionFactory,
                                                          @ReceiverInboundChunkChannel MessageChannel inboundMessageChannel){
        return IntegrationFlow
                            .from(Amqp.inboundAdapter(connectionFactory,transmitterConfig.getOutbound()))
                            .channel(inboundMessageChannel)
                            .get();
    }

    @Bean
    public IntegrationFlow receiverOutboundIntegrationFlow(@ReceiverOutboundChunkChannel MessageChannel outboundMessageChannel,
                                                           AmqpTemplate amqpTemplate){
        return IntegrationFlow
                            .from(outboundMessageChannel)
                            .handle(Amqp.outboundAdapter(amqpTemplate).routingKey(transmitterConfig.getInbound()))
                            .get();
    }

    @Bean
    @ReceiverItemProcessor
    public ItemProcessor<Object,Object> receiverItemProcessor(){
        return item -> item;
    }

    @Bean
    @ReceiverItemWriter
    public ItemWriter<Object> receiverItemWriter(){
        return chunk -> {
            log.info("doing the long-running writing thing");
            List<?> items = chunk.getItems();
            for (var i : items)
                log.info("i={}", i + "");
        };
    }
}
