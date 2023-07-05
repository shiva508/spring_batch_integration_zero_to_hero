package com.pool.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.core.GenericHandler;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.ftp.dsl.Ftp;
import org.springframework.integration.ftp.session.DefaultFtpSessionFactory;
import org.springframework.integration.ftp.session.FtpRemoteFileTemplate;
import org.springframework.messaging.MessageHeaders;
import org.springframework.util.Assert;

import java.io.File;
import java.util.concurrent.TimeUnit;

//@Configuration
@Slf4j
@Profile("inbound")
public class InboundConfiguration {

    @Bean
    public IntegrationFlow integrationFlow(DefaultFtpSessionFactory defaultFtpSessionFactory){
        var file=new File(new File(System.getProperty("user.home","Desktop")),"local");
        Assert.isTrue(file.exists() || file.mkdir(),"Directory could not be created and exited");
        var ftp= Ftp.inboundAdapter(defaultFtpSessionFactory)//
                    .autoCreateLocalDirectory(true)//
                    .patternFilter("*.txt")//
                    .localDirectory(file);



        return IntegrationFlow.from(ftp,spcas -> spcas.poller(pf -> pf.fixedRate(1000, TimeUnit.MILLISECONDS)))
                .handle((GenericHandler<File>) (payload, headers) -> {
                    log.info("Path :"+payload.getAbsolutePath());
                    return null;
                }).get();
    }

    @Bean
    public DefaultFtpSessionFactory defaultFtpSessionFactory(@Value("${ftp2.username}") String username,
                                                             @Value("${ftp2.password}") String password,
                                                             @Value("${ftp2.host}") String host,
                                                             @Value("${ftp2.port}") int port){
       return new DefaultFtpSessionFactory(){
           {
               setUsername(username);
               setPassword(password);
               setHost(host);
               setPort(port);
           }
       };
    }

}
