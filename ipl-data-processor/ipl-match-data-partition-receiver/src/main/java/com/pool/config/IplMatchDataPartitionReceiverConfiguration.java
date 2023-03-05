package com.pool.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pool.config.kafka.KafkaConfig;
import com.pool.record.Response;
import com.pool.record.Transaction;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.GenericTransformer;
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

    private final ObjectMapper objectMapper;

    public IplMatchDataPartitionReceiverConfiguration(KafkaTemplate<String, String> kafkaTemplate,
                                                      KafkaConfig kafkaConfig,
                                                      ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaConfig=kafkaConfig;
        this.objectMapper=objectMapper;
    }

    @Bean
    public MessageChannel toKafkaMessageChannel(){
        return MessageChannels.direct().get();
    }

    @Bean
    public DirectChannel fromKafkaMessageChannel(){
        return MessageChannels.direct().get();
    }

    @Bean("receiveFromKafkaFlow")
    public IntegrationFlow receiveFromKafkaFlow(ConsumerFactory<String, String> consumerFactory) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,kafkaConfig.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "siTestGroup");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, 10);
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 15000);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        ConsumerProperties consumerProperties=new ConsumerProperties("kReplies");
        consumerProperties.setKafkaConsumerProperties(props);
        return IntegrationFlow.from(Kafka.inboundChannelAdapter(consumerFactory,consumerProperties))
               /* .handle((payload, headers) -> {
                    System.out.println("OPT:::::::"+payload);
                    return new Response("1","1").toString();
                })*/
                .transform(new GenericTransformer<Object, Object>() {
                    @Override
                    public Object transform(Object source) {
                        System.out.println(source);
                        return source;
                    }
                })
                .channel(toKafkaMessageChannel())
                .get();
        }

   /* @Bean("receiveFromKafkaFlow")
    public IntegrationFlow receiveFromKafkaFlowOne() {

        return IntegrationFlow.from(fromKafkaMessageChannel())
                .handle((payload, headers) -> {
                    System.out.println("OPT:::::::"+payload);
                    return new Response("1","1").toString();
                })
                .channel(toKafkaMessageChannel())
                .get();
    }*/
   @Bean("sendToKafkaFlow")
    public IntegrationFlow sendToKafkaFlow() {
        return IntegrationFlow.from(toKafkaMessageChannel())
                //.handle(Kafka.outboundChannelAdapter(kafkaTemplate).topic("kReplies"))
                .handle(message -> {
                    System.out.println("message:::::"+message);
                })
                .get();
    }

    public String convertToJson(Transaction transaction){
        try {
            return objectMapper.writeValueAsString(transaction);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
