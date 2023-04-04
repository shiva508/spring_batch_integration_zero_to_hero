package com.pool.config.integration;

import com.pool.record.IplData;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.core.GenericHandler;
import org.springframework.integration.core.GenericTransformer;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.messaging.MessageChannel;

@Configuration
@ConditionalOnProperty(name = "integration.rest",havingValue = "true")
public class RestApiIntegrationConfiguration {
    @Bean(name = "transmitterChannelRest")
    public MessageChannel transmitterChannelRest(){
        return MessageChannels.direct().get();
    }

    @Bean
    public IntegrationFlow restTransmitterIntegrationFlow(){
        return IntegrationFlow.from(transmitterChannelRest())
                .transform((GenericTransformer<IplData, IplData>) source -> new IplData(source.year(),source.winner().equals("DC")?"Hyderabad Deccan Chargers":"Sunrisers Hyderabad"))
                .transform((GenericTransformer<IplData, IplData>) source -> new IplData(source.year(),source.winner().toUpperCase()))
                .handle((GenericHandler<IplData>) (payload, headers) -> {
                    System.out.println(payload);
                    return null;
                })
                .get();
    }

    @Bean
    public IntegrationFlow restReceiverIntegrationFlow(){
        return IntegrationFlow.from(transmitterChannelRest())
                .handle((GenericHandler<String>) (payload, headers) -> {
                    System.out.println("Received Message: "+payload);
                    return null;
                }).get();
    }
}
