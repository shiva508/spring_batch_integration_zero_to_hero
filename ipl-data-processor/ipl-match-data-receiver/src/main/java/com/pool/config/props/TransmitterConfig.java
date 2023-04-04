package com.pool.config.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "transmitter.rabbitmq.queue")
public class TransmitterConfig {

    private String outbound;

    private String inbound;

    private String exchange;
}
