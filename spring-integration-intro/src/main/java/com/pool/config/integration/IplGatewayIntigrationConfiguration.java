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
import org.springframework.messaging.MessageHeaders;

@Configuration
@ConditionalOnProperty(name = "integration.gateway",havingValue = "true")
public class IplGatewayIntigrationConfiguration {
    @Bean(name = "iplGatewayTxMessageChannel")
    public MessageChannel iplGatewayTxMessageChannel(){
        return MessageChannels.direct().get();
    }

    @Bean(name = "iplGatewayRxMessageChannel")
    public MessageChannel iplGatewayRxMessageChannel(){
        return MessageChannels.direct().get();
    }
    @Bean
    public IntegrationFlow gatewayTransmitterIntegrationFlow(){
        return IntegrationFlow.from(iplGatewayTxMessageChannel())
                .transform((GenericTransformer<IplData, IplData>) source -> new IplData(source.year(),source.winner().equals("DC")?"Hyderabad Deccan Chargers":"Sunrisers Hyderabad"))
                .transform((GenericTransformer<IplData, IplData>) source -> new IplData(source.year(),source.winner().toUpperCase()))
                .channel(iplGatewayRxMessageChannel())
                .get();
    }
   /* @Bean
    public IntegrationFlow gatewayReceiverIntegrationFlow(){
        return IntegrationFlow.from(iplGatewayRxMessageChannel())
                .handle((GenericHandler<IplData>) (payload, headers) -> {
                    System.out.println("We got msg through Gateway: "+payload);
                    return payload;
                })
                .get();
    }*/
}
