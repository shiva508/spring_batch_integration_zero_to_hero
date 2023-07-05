package com.pool.config;

import com.pool.record.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.core.GenericHandler;
import org.springframework.integration.core.GenericTransformer;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.messaging.MessageChannel;

@Configuration
public class IntroConfiguration {
    @Bean
    public MessageChannel userMessageChannel(){
        return new DirectChannel();
    }

    @Bean
    public MessageChannel userNameMessageChannel(){
        return new DirectChannel();
    }

    @Bean
    public QueueChannel userQueueChannel(){
        return new QueueChannel();
    }

    @Bean
    public IntegrationFlow userIntegrationFlow(){
        return IntegrationFlow.from(userMessageChannel())
                              .transform((GenericTransformer<User,String>)(user)-> user.name()+"("+user.level()+")")
                              .channel(userNameMessageChannel())
                              .get();
    }
    @Bean
    public IntegrationFlow userNameIntegrationFlow(){
        return IntegrationFlow.from(userNameMessageChannel())
                .handle((GenericHandler<String>)(payload,header)->{
                    System.out.println(payload);
                    return payload;
                })
                .channel(userQueueChannel())
                .get();
    }
}
