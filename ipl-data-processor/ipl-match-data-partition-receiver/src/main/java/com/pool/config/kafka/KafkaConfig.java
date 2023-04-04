package com.pool.config.kafka;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "receiver.kafka.consumer")
@Data
public class KafkaConfig {
    private String bootstrapServers;
}
