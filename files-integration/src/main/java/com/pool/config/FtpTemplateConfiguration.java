package com.pool.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.ftp.session.DefaultFtpSessionFactory;
import org.springframework.integration.ftp.session.FtpRemoteFileTemplate;

import java.io.File;
import java.io.FileOutputStream;

@Configuration
@Slf4j
@Profile("prod")
public class FtpTemplateConfiguration {

    @Value("${ftp.file.name}")
    private String filename;

   // @Bean
    public InitializingBean initializingBeanFile(FtpRemoteFileTemplate ftpRemoteFileTemplate){
        return  () ->ftpRemoteFileTemplate.execute(session -> {
            var file = new File(new File(System.getProperty("user.home"), "Desktop"), "hello-local.txt");
            try (var fout = new FileOutputStream(file)) {
                session.read("shiva.txt", fout);
            }
            log.info("read " + file.getAbsolutePath());
            return null;
        });
    }

    @Bean
    public InitializingBean initializingBean(FtpRemoteFileTemplate ftpRemoteFileTemplate){
        return  () ->ftpRemoteFileTemplate.execute(session -> {
            var file = new File(new File(System.getProperty("user.home"), "Desktop"), "shiva_image.JPG");
            try (var fout = new FileOutputStream(file)) {
                session.read("P34A6686.JPG", fout);
            }
            log.info("read " + file.getAbsolutePath());
            return null;
        });
    }

    @Bean
    public FtpRemoteFileTemplate ftpRemoteFileTemplate(DefaultFtpSessionFactory defaultFtpSessionFactory){
        return new FtpRemoteFileTemplate(defaultFtpSessionFactory);
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
