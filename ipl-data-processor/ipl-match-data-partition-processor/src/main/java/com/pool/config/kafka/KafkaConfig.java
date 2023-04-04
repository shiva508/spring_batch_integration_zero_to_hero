package com.pool.config.kafka;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "transmitter.kafka.producer")
@Data
public class KafkaConfig {
    private String bootstrapServers;
}
