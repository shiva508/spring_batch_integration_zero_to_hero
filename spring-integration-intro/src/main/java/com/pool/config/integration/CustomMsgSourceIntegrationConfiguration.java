package com.pool.config.integration;

import com.pool.msg.IplMessageSource;
import com.pool.record.IplData;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.core.GenericHandler;
import org.springframework.integration.core.GenericTransformer;
import org.springframework.integration.dsl.IntegrationFlow;

import java.util.concurrent.TimeUnit;

@Configuration
@ConditionalOnProperty(name = "integration.msg-cource",havingValue = "true")
public class CustomMsgSourceIntegrationConfiguration {
    @Bean
    public IntegrationFlow customMsgSourceintegrationFlow(IplMessageSource iplMessageSource){
       return IntegrationFlow.from(iplMessageSource,spcas -> spcas.poller(pf -> pf.fixedRate(5, TimeUnit.SECONDS)))
               .transform((GenericTransformer<IplData, IplData>) source -> new IplData(source.year(),source.winner().equals("DC")?"Hyderabad Deccan Chargers":"Sunrisers Hyderabad"))
               .transform((GenericTransformer<IplData, IplData>) source -> new IplData(source.year(),source.winner().toUpperCase()))
               .handle((GenericHandler<IplData>) (payload, headers) -> {
                   System.out.println(payload);
                   return null;
               })
               .get();
    }
}
