package com.pool.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "tweet.csv")
public class TweetCsvConfiguration {

    private String columns;
}
