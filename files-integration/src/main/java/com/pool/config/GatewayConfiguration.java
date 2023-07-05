package com.pool.config;

import org.apache.commons.net.ftp.FTPFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.expression.common.LiteralExpression;
import org.springframework.integration.core.GenericHandler;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.file.remote.gateway.AbstractRemoteFileOutboundGateway;
import org.springframework.integration.file.remote.session.DelegatingSessionFactory;
import org.springframework.integration.file.support.FileExistsMode;
import org.springframework.integration.ftp.dsl.Ftp;
import org.springframework.integration.ftp.session.DefaultFtpSessionFactory;
import org.springframework.integration.ftp.session.FtpRemoteFileTemplate;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;
import java.util.Map;
import static org.springframework.web.servlet.function.RouterFunctions.route;

//@Configuration
public class GatewayConfiguration {


    @Bean
    public RouterFunction<ServerResponse> responseRouterFunction(){
        var in = this.incomingMessageChannel();
        return route()
                .POST("/store/{sfn}", request -> {
                    var name = request.pathVariable("sfn");
                    var msg = MessageBuilder.withPayload(name).build();
                    var sent = in.send(msg);
                    return ServerResponse.ok().body(sent);
                })
                .build();
    }
    @Bean
    public IntegrationFlow gateWayIntegrationFlow(FtpRemoteFileTemplate ftpRemoteFileTemplate,
                                                  DelegatingSessionFactory<FTPFile> factoryMap){
        return flow -> flow.channel(incomingMessageChannel())
                .handle((payload, headers) -> {
                    factoryMap.setThreadKey(payload);
                    return payload;
                }).handle(Ftp.outboundGateway(ftpRemoteFileTemplate,AbstractRemoteFileOutboundGateway.Command.PUT,"payload")
                             .fileExistsMode(FileExistsMode.IGNORE)
                             .options(AbstractRemoteFileOutboundGateway.Option.RECURSIVE)
                ).handle((payload, headers) -> {
                    factoryMap.clearThreadKey();
                    return null;
                });
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
    @Bean
    public MessageChannel incomingMessageChannel(){
        return MessageChannels.direct().get();
    }
    @Bean
    public FtpRemoteFileTemplate ftpRemoteFileTemplate(DelegatingSessionFactory<FTPFile> factoryMap){
        FtpRemoteFileTemplate fileTemplate=new FtpRemoteFileTemplate(factoryMap);
        fileTemplate.setRemoteDirectoryExpression(new LiteralExpression(""));
        return fileTemplate;

    }
    @Bean
    public DelegatingSessionFactory<FTPFile>  ftpFileDelegatingSessionFactory(Map<String,DefaultFtpSessionFactory> factoryMap){
        return new DelegatingSessionFactory<>(factoryMap::get);
    }

    @Bean
    public DefaultFtpSessionFactory factoryTwo(@Value("${ftp2.username}") String username,
                                               @Value("${ftp2.password}") String password,
                                               @Value("${ftp2.host}") String host,
                                               @Value("${ftp2.port}") int port){
        return this.buildDefaultFtpSessionFactory(username,password,host,port);
    }

    @Bean
    public DefaultFtpSessionFactory factoryOne(@Value("${ftp1.username}") String username,
                                               @Value("${ftp1.password}") String password,
                                               @Value("${ftp1.host}") String host,
                                               @Value("${ftp1.port}") int port){
        return this.buildDefaultFtpSessionFactory(username,password,host,port);
    }

    public DefaultFtpSessionFactory buildDefaultFtpSessionFactory(String username,String password,
                                                                  String host,int port){
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
