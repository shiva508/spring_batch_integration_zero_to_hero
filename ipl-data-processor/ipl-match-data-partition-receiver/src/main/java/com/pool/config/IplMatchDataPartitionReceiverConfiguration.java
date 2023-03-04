package com.pool.config;

import com.pool.config.kafka.KafkaConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.kafka.dsl.Kafka;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ConsumerProperties;
import org.springframework.messaging.MessageChannel;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
public class IplMatchDataPartitionReceiverConfiguration {

    private final KafkaTemplate<String,String> kafkaTemplate;

    private final KafkaConfig kafkaConfig;




    public IplMatchDataPartitionReceiverConfiguration(KafkaTemplate<String, String> kafkaTemplate,
                                                      KafkaConfig kafkaConfig) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaConfig=kafkaConfig;
    }

    @Bean
    public MessageChannel toKafkaMessageChannel(){
        return MessageChannels.direct().get();
    }

    @Bean
    public DirectChannel fromKafkaMessageChannel(){
        return MessageChannels.direct().get();
    }

    @Bean
    public IntegrationFlow inbound(ConsumerFactory<String, String> consumerFactory) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,kafkaConfig.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "siTestGroup");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, 100);
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 15000);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        ConsumerProperties consumerProperties=new ConsumerProperties("kRequests");
        consumerProperties.setKafkaConsumerProperties(props);
        return IntegrationFlow.from(Kafka.inboundChannelAdapter(consumerFactory,consumerProperties))
                .channel(fromKafkaMessageChannel())
                .transform(obj->{
                    System.out.println(obj);
                    return obj;
                })
                .get();
    }


}
