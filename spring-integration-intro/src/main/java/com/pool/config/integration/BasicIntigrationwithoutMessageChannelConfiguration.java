package com.pool.config.integration;

import com.pool.record.IplData;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.core.GenericHandler;
import org.springframework.integration.core.GenericTransformer;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.support.MessageBuilder;

import java.util.Random;

@Configuration
@ConditionalOnProperty(name = "integration.msg",havingValue = "true")
public class BasicIntigrationwithoutMessageChannelConfiguration {

    public IplData iplScheduler(){
        return new IplData(String.valueOf(new Random().nextInt(2008,2023)),Math.random() <.5?"DC":"SRH");
    }
    @Bean
    public IntegrationFlow integrationFlowTx(){
       return IntegrationFlow.from((MessageSource<IplData>) () -> MessageBuilder.withPayload(iplScheduler()).build(),poller -> poller.poller(pf -> pf.fixedRate(100)))
               .transform((GenericTransformer<IplData, IplData>) source -> new IplData(source.year(),source.winner().equals("DC")?"Hyderabad Deccan Chargers":"Sunrisers Hyderabad"))
               .transform((GenericTransformer<IplData, IplData>) source -> new IplData(source.year(),source.winner().toUpperCase()))
               .handle((GenericHandler<IplData>) (payload, headers) -> {
                   System.out.println(payload);
                   return null;
               })
               .get();
    }
}
