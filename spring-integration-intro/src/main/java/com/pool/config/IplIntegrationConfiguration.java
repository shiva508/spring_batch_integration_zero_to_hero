package com.pool.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pool.record.IplData;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.json.JsonToObjectTransformer;
import org.springframework.integration.json.ObjectToJsonTransformer;
import org.springframework.integration.support.json.Jackson2JsonObjectMapper;

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


    @Bean
    @Transformer(inputChannel = "transmitterMessageChannel.toJson",outputChannel = "transmitterMessageChannel.toJson")
    public ObjectToJsonTransformer objectToJsonTransformer(){
        return new ObjectToJsonTransformer(mapper());
    }

    @Bean
    public Jackson2JsonObjectMapper mapper() {
        ObjectMapper objectMapper=new ObjectMapper();
        return new Jackson2JsonObjectMapper(objectMapper);
    }

    @Bean
    @Transformer(inputChannel = "receiverMessageChannel",outputChannel = "receiverMessageChannel")
    public JsonToObjectTransformer jsonToObjectTransformer(){
        return new JsonToObjectTransformer(IplData.class);
    }
}
