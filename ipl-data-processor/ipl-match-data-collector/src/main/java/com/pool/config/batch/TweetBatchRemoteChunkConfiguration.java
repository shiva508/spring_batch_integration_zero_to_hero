package com.pool.config.batch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pool.config.props.TransmitterConfig;
import com.pool.record.Tweet;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.integration.chunk.ChunkMessageChannelItemWriter;
import org.springframework.batch.integration.chunk.RemoteChunkHandlerFactoryBean;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.integration.amqp.dsl.Amqp;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

//@Configuration
public class TweetBatchRemoteChunkConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final TransmitterConfig transmitterConfig;
    private final ObjectMapper objectMapper;
    public TweetBatchRemoteChunkConfiguration(JobRepository jobRepository,
                                              PlatformTransactionManager transactionManager,
                                              TransmitterConfig transmitterConfig,
                                              ObjectMapper objectMapper) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.transmitterConfig = transmitterConfig;
        this.objectMapper = objectMapper;
    }

    @Bean
    public Job tweetJob(@Qualifier("tweetStep") Step tweetStep) {
        return new JobBuilder("tweetJob",jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(tweetStep)
                .build();
    }

    @Bean("tweetStep")
    public TaskletStep tweetStep(@Qualifier("tweetItemReader") ItemReader<Tweet> tweetItemReader,
                                 @Qualifier("tweet2taskExecutor") TaskExecutor tweet2taskExecutor){
        return new StepBuilder("tweetStep",jobRepository)
                .<Tweet,String>chunk(500,transactionManager)
                .reader(tweetItemReader)//Read data from CSV
                .processor(this::convertToJson)//Convert java to JSON
                .writer(chunkMessageChannelItemWriter())//Write Data to remote chunk
                .taskExecutor(tweet2taskExecutor)//Task executor to handle async
                .build();
    }

    @Bean("tweetItemReader")
    public FlatFileItemReader<Tweet> tweetItemReader(@Value("${tweet.csv.file.path}") Resource resource){
        return new FlatFileItemReaderBuilder<Tweet>()
                .resource(resource)
                .name("tweetItemReader")
                .linesToSkip(1)
                .delimited().delimiter(",")
                .names("date,content,hashtags,like_count,rt_count,followers_count,isVerified,language,coordinates,place,source".split(","))
                .fieldSetMapper(fieldSet -> new Tweet(fieldSet.readDate("date"),
                        fieldSet.readString("content")))
                .build();
    }

    @Bean("tweetTaskExecutor")
    public TaskExecutor tweetTaskExecutor(){
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(4);
        taskExecutor.setMaxPoolSize(4);
        taskExecutor.afterPropertiesSet();
        return taskExecutor;
    }
    @Bean("tweet2taskExecutor")
    public TaskExecutor tweet2taskExecutor() {
        return new SimpleAsyncTaskExecutor("tweet_batch");
    }

    //Convert java object to JSON format before sending to RABBITMQ
    public String convertToJson(Tweet tweet){
        try {
            return objectMapper.writeValueAsString(tweet);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    //ADD OUTBOUND MESSAGE CHANNEL TO SEND REQUEST
    @Bean
    public DirectChannel outbountDirectChannel(){
        return MessageChannels.direct().get();
    }

    //ADD INBOUND MESSAGE CHANNEL TO LISTEN TO  RESPONSE
    @Bean
    public QueueChannel inboundQueueChannel(){
        return MessageChannels.queue().get();
    }

    //NEED MessagingTemplate TEMPLATE send request
    @Bean
    public MessagingTemplate messagingTemplate(){
        MessagingTemplate messagingTemplate=new MessagingTemplate();
        messagingTemplate.setDefaultChannel(outbountDirectChannel());
        messagingTemplate.setReceiveTimeout(2000);
        return messagingTemplate;
    }

    //Create Chunk Writer
    // 1. Connects to MessageTemplate(Sends request to Rabbitmq)
    @Bean
    @StepScope
    public ChunkMessageChannelItemWriter<String> chunkMessageChannelItemWriter(){
        ChunkMessageChannelItemWriter<String> messageChannelItemWriter=new ChunkMessageChannelItemWriter<>();
        messageChannelItemWriter.setMessagingOperations(messagingTemplate());
        messageChannelItemWriter.setReplyChannel(inboundQueueChannel());
        return messageChannelItemWriter;
    }

    @Bean
    public RemoteChunkHandlerFactoryBean<String> remoteChunkHandler(TaskletStep tweetStep,
                                                                    ChunkMessageChannelItemWriter<String> chunkMessageChannelItemWriter){
        RemoteChunkHandlerFactoryBean<String> handlerFactory=new RemoteChunkHandlerFactoryBean<>();
        handlerFactory.setChunkWriter(chunkMessageChannelItemWriter);
        handlerFactory.setStep(tweetStep);
        return handlerFactory;
    }

    @Bean
    public IntegrationFlow outboundIntegrationFlow(AmqpTemplate amqpTemplate){
        return IntegrationFlow
                .from(outbountDirectChannel())
                .handle(Amqp.outboundAdapter(amqpTemplate).routingKey(transmitterConfig.getOutbound()))
                .get();
    }

    @Bean
    public IntegrationFlow inboundIntegrationFlow(ConnectionFactory connectionFactory){
        return IntegrationFlow.from(Amqp.inboundAdapter(connectionFactory,transmitterConfig.getInbound()))
                .channel(inboundQueueChannel())
                .get();

    }
}
