package com.pool.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationComponentSpec;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;

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
       return IntegrationFlow.from("transmitterMessageChannel")
               .handle((payload, headers) -> {
                   System.out.println(payload);
                   return "Hi!";
               })
               .get();
    }

    //@Bean("receiverIntegrationFlow")
    public IntegrationFlow receiverIntegrationFlow(){
      return IntegrationFlow.from(receiverMessageChannel())
              .bridge(IntegrationComponentSpec::start)
                .get();
    }
}
