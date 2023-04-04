package com.pool.config.integration;

import com.pool.msg.IplMessageSource;
import com.pool.record.IplData;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.core.GenericHandler;
import org.springframework.integration.core.GenericTransformer;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.context.IntegrationFlowContext;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Configuration
@ConditionalOnProperty(name = "integration.param",havingValue = "true")
public class ParameterisedIntegrationConfiguration {

    @Bean
    ApplicationRunner applicationRunner(IplMessageSource iplMessageSource, IntegrationFlowContext integrationFlowContext){
        return args -> {
            var integrationFlowOne = customMsgSourceintegrationFlow(iplMessageSource, 1);
            var integrationFlowTwo = customMsgSourceintegrationFlow(iplMessageSource, 4);
            Set.of(integrationFlowOne,integrationFlowTwo).forEach(integrationFlow -> integrationFlowContext.registration(integrationFlow).register().start());
        };
    }
        public IntegrationFlow customMsgSourceintegrationFlow(IplMessageSource iplMessageSource,int fixedRate){
            return IntegrationFlow.from(iplMessageSource,spcas -> spcas.poller(pf -> pf.fixedRate(fixedRate, TimeUnit.SECONDS)))
                    .transform((GenericTransformer<IplData, IplData>) source -> new IplData(source.year(),source.winner().equals("DC")?"Hyderabad Deccan Chargers":"Sunrisers Hyderabad"))
                    .transform((GenericTransformer<IplData, IplData>) source -> new IplData(source.year(),source.winner().toUpperCase()))
                    .handle((GenericHandler<IplData>) (payload, headers) -> {
                        System.out.println(payload);
                        return null;
                    })
                    .get();
        }
}
