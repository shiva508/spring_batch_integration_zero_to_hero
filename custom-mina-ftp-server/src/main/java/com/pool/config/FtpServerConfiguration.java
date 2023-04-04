package com.pool.config;


import com.pool.config.manager.FtpUserManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.filesystem.nativefs.NativeFileSystemFactory;
import org.apache.ftpserver.ftplet.FileSystemFactory;
import org.apache.ftpserver.ftplet.Ftplet;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.listener.Listener;
import org.apache.ftpserver.listener.ListenerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;

import java.io.File;
import java.util.Collections;
import java.util.Map;

@Configuration
@Slf4j
public class FtpServerConfiguration {

    @Bean
   public FileSystemFactory fileSystemFactory() {
        NativeFileSystemFactory fileSystemFactory = new NativeFileSystemFactory();
        fileSystemFactory.setCreateHome(true);
        fileSystemFactory.setCaseInsensitive(false);
        return fileSystemFactory::createFileSystemView;
    }

    @Bean
    public Listener listener(@Value("${ftp.port:2121}") int port){
        ListenerFactory listenerFactory=new ListenerFactory();
        listenerFactory.setPort(port);
        return listenerFactory.createListener();
    }

    @Bean
    FtpServer ftpServer(Map<String, Ftplet> ftpletMap,
                        UserManager userManager,
                        Listener nioListener,
                        FileSystemFactory fileSystemFactory){
        FtpServerFactory ftpServerFactory = new FtpServerFactory();
        ftpServerFactory.setListeners(Collections.singletonMap("default", nioListener));
        ftpServerFactory.setFileSystem(fileSystemFactory);
        ftpServerFactory.setFtplets(ftpletMap);
        ftpServerFactory.setUserManager(userManager);
        return ftpServerFactory.createServer();
    }

    @Bean
    public DisposableBean stopFtpServer(FtpServer ftpServer){
        return ftpServer::stop;
    }
    @Bean
    public InitializingBean startFtpServer(FtpServer ftpServer){
        return ftpServer::start;
    }

    @Bean
    public  UserManager userManager(@Value("${ftp.root:${HOME}/Desktop/root}") File root,
                                    JdbcTemplate template){
        Assert.isTrue(root.exists() || root.mkdirs(), "the root directory must exist.");
        return new FtpUserManager(root, template);
    }
}
