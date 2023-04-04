package com.pool.config.batch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pool.config.props.TransmitterConfig;
import com.pool.record.Tweet;
import com.pool.transmitter.annotation.TransmitterChunkStep;
import com.pool.transmitter.annotation.TransmitterInboundChunkChannel;
import com.pool.transmitter.annotation.TransmitterItemWriter;
import com.pool.transmitter.annotation.TransmitterOutboundChunkChannel;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.integration.amqp.dsl.Amqp;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.messaging.MessageChannel;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@ConditionalOnProperty(value = "batch.chunk.transmitter",havingValue = "true")
public class TweetBatchRemoteChunkAutoConfiguration {

    private final JobRepository jobRepository;

    private final PlatformTransactionManager transactionManager;

    private final ObjectMapper objectMapper;

    private final TransmitterConfig transmitterConfig;

    public TweetBatchRemoteChunkAutoConfiguration(JobRepository jobRepository,
                                                  PlatformTransactionManager transactionManager,
                                                  ObjectMapper objectMapper,
                                                  TransmitterConfig transmitterConfig) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.objectMapper = objectMapper;
        this.transmitterConfig = transmitterConfig;
    }

    @Bean
    public IntegrationFlow iplOutboundIntegrationFlow(@TransmitterOutboundChunkChannel MessageChannel iplOutboundMessageChannel,
                                                       AmqpTemplate amqpTemplate){
        return IntegrationFlow
                .from(iplOutboundMessageChannel)
                .handle(Amqp.outboundAdapter(amqpTemplate).routingKey(transmitterConfig.getOutbound()))
                .get();
    }
    @Bean
    public IntegrationFlow iplInboundIntegrationFlow(ConnectionFactory connectionFactory,
                                                     @TransmitterInboundChunkChannel MessageChannel iplInboundMessageChannel){
        return IntegrationFlow
                .from(Amqp.inboundAdapter(connectionFactory,transmitterConfig.getInbound()))
                .channel(iplInboundMessageChannel)
                .get();
    }
    @Bean
    public Job iplJob(Step iplStep){
        return new JobBuilder("iplJob",jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(iplStep)
                .build();
    }

    @Bean("iplTaskletStep")
    @TransmitterChunkStep
    public TaskletStep iplTaskletStep(@Qualifier("iplFlatFileItemReader") ItemReader<Tweet> iplFlatFileItemReader,
                                      @TransmitterItemWriter ItemWriter<String> iplItemWriter){
        return new StepBuilder("iplTaskletStep",jobRepository)
                .<Tweet,String>chunk(500,transactionManager)
                .reader(iplFlatFileItemReader)
                .processor(this::convertToJson)
                .writer(iplItemWriter)
                .build();
    }

    @Bean("iplFlatFileItemReader")
    public FlatFileItemReader<Tweet> iplFlatFileItemReader(@Value("${tweet.csv.file.path}")Resource resource){
        return new FlatFileItemReaderBuilder<Tweet>()
                .name("iplFlatFileItemReader")
                .linesToSkip(1)
                .resource(resource)
                .delimited().delimiter(",")
                .names("date,content,hashtags,like_count,rt_count,followers_count,isVerified,language,coordinates,place,source".split(","))
                .fieldSetMapper(fieldSet -> new Tweet(fieldSet.readDate("date"),fieldSet.readString("content")))
                .build();
    }

    //Convert java object to JSON format before sending to RABBITMQ
    public String convertToJson(Tweet tweet){
        try {
            System.out.println(tweet);
            return objectMapper.writeValueAsString(tweet);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
