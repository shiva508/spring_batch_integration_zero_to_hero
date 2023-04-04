package com.pool;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.pool.*")
public class AutoConfigRemoteChunkApplication {
    public static void main(String[] args) {
        SpringApplication.run(AutoConfigRemoteChunkApplication.class, args);
    }
}