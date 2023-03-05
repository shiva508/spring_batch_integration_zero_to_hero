package com.pool.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.GenericHandler;
import org.springframework.integration.dsl.GenericEndpointSpec;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.handler.BridgeHandler;
import org.springframework.messaging.MessageHeaders;

import java.util.function.Consumer;

@Configuration
public class IplIntegrationConfiguration {
    @Bean("transmitterMessageChannel")
    public DirectChannel transmitterMessageChannel(){
        return MessageChannels.direct().get();
    }

    @Bean("receiverMessageChannel")
    public DirectChannel receiverMessageChannel(){
        return MessageChannels.direct().get();
    }

    @Bean("transmitterIntegrationFlow")
    public IntegrationFlow transmitterIntegrationFlow(){
       return IntegrationFlow.from(transmitterMessageChannel())
               .handle((payload, headers) -> {
                   System.out.println(payload);
                   return payload;
               })
               .channel(receiverMessageChannel())
               .get();
    }

    @Bean("receiverIntegrationFlow")
    public IntegrationFlow receiverIntegrationFlow(){
      return IntegrationFlow.from(receiverMessageChannel())
              .bridge(new Consumer<GenericEndpointSpec<BridgeHandler>>() {
                  @Override
                  public void accept(GenericEndpointSpec<BridgeHandler> bridgeHandlerGenericEndpointSpec) {
                      bridgeHandlerGenericEndpointSpec.start();
                  }
              })
                .get();
    }
}
