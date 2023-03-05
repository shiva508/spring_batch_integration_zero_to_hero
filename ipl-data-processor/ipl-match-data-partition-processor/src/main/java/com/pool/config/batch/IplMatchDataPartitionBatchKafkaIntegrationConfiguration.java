package com.pool.config.batch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pool.config.kafka.KafkaConfig;
import com.pool.record.Transaction;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.partition.PartitionHandler;
import org.springframework.batch.core.partition.support.MultiResourcePartitioner;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.MultiResourceItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.integration.IntegrationMessageHeaderAccessor;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.kafka.dsl.Kafka;
import org.springframework.integration.kafka.dsl.KafkaProducerMessageHandlerSpec;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConsumerProperties;
import org.springframework.kafka.support.DefaultKafkaHeaderMapper;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.PollableChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Stream;

@Configuration
public class IplMatchDataPartitionBatchKafkaIntegrationConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final ConfigurableApplicationContext context;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ProducerFactory<String, String> producerFactory;
    private final ObjectMapper objectMapper;


    private final KafkaConfig kafkaConfig;

    @Value("file://${HOME}/shiva/mywork/assignment/dada/spring_batch_integration_zero_to_hero/data/partition/csv/transactions*.csv")
    private Resource[] inputResources;

    public IplMatchDataPartitionBatchKafkaIntegrationConfiguration(JobRepository jobRepository,
                                                    PlatformTransactionManager transactionManager,
                                                    ConfigurableApplicationContext context,
                                                    KafkaTemplate<String, String> kafkaTemplate,
                                                    ProducerFactory<String, String> producerFactory,
                                                    ObjectMapper objectMapper,
                                                                   KafkaConfig kafkaConfig) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.context = context;
        this.kafkaTemplate=kafkaTemplate;
        this.producerFactory=producerFactory;
        this.objectMapper=objectMapper;
        this.kafkaConfig=kafkaConfig;
    }

    @Bean
    @StepScope
    public MultiResourcePartitioner partitioner() {
        MultiResourcePartitioner partitioner = new MultiResourcePartitioner();
        partitioner.setKeyName("file");
        partitioner.setResources(inputResources);
        return partitioner;
    }


    @Bean
    @StepScope
    public FlatFileItemReader<Transaction> fileTransactionReader(
            @Value("#{stepExecutionContext['file']}") Resource resource) {
        return new FlatFileItemReaderBuilder<Transaction>()
                .name("flatFileTransactionReader")
                .resource(resource)
                .delimited()
                .names(new String[] {"account", "amount", "timestamp"})
                .fieldSetMapper(fieldSet -> new Transaction(
                        fieldSet.readString("account"),
                        fieldSet.readDate("timestamp", "yyyy-MM-dd HH:mm:ss"),
                        fieldSet.readBigDecimal("amount"))
                ).build();
    }

    @Bean
    public PartitionHandler partitionHandler() {
        TaskExecutorPartitionHandler retVal = new TaskExecutorPartitionHandler();
        retVal.setTaskExecutor(taskExecutor());
        retVal.setStep(step1());
        retVal.setGridSize(10);
        return retVal;
    }

    @Bean
    public TaskExecutor taskExecutor() {
        return new SimpleAsyncTaskExecutor("spring_batch"+new Date());
    }
    @Bean
    @StepScope
    public JdbcBatchItemWriter<Transaction> writer(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Transaction>()
                .dataSource(dataSource)
                .beanMapped()
                .sql("INSERT INTO TRANSACTION (ACCOUNT, AMOUNT, TIMESTAMP) VALUES (:account, :amount, :timestamp)")
                .build();
    }

    @Bean
    public Step partitionedMaster() {
        return new StepBuilder("step1",jobRepository)
                .partitioner("step1", partitioner())
                .step(step1())
                .partitionHandler(partitionHandler())
                .build();
    }

    @Bean
    //@StepScope
    public Step step1() {
        Map<String, Object> headers=new LinkedHashMap<>();
        headers.put("kafka_topic","kRequests");
        return new StepBuilder("step1",jobRepository)
                .<Transaction, Transaction>chunk(100,transactionManager)
                .reader(fileTransactionReader(null))
                .processor(transaction -> {
                    toKafkaMessageChannel().send(new GenericMessage<>(convertToJson(transaction),headers));
                    return transaction;
                })
                .writer(writer(null))
                .build();
    }

    @Bean
    @StepScope
    public MultiResourceItemReader<Transaction> multiResourceItemReader() {
        return new MultiResourceItemReaderBuilder<Transaction>()
                .delegate(delegate())
                .name("multiresourceReader")
                .resources(inputResources)
                .build();
    }

    @Bean
    public FlatFileItemReader<Transaction> delegate() {
        return new FlatFileItemReaderBuilder<Transaction>()
                .name("flatFileTransactionReader")
                .delimited()
                .names(new String[] {"account", "amount", "timestamp"})
                .fieldSetMapper(fieldSet -> new Transaction(fieldSet.readString("account"),
                        fieldSet.readDate("timestamp", "yyyy-MM-dd HH:mm:ss"),
                        fieldSet.readBigDecimal("amount")))
                .build();
    }

    @Bean
    public Job parallelStepsJob() {
        return new JobBuilder("parallelStepsJob"+new Date(),jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(partitionedMaster())
                .build();
    }


    @Bean("toKafkaMessageChannel")
    public MessageChannel toKafkaMessageChannel(){
        return MessageChannels.direct().get();
    }

    @Bean("fromKafkaMessageChannel")
    public DirectChannel fromKafkaMessageChannel(){
        return MessageChannels.direct().get();
    }


    /*=======================Kafka Code======================*/

    @Bean
    public DefaultKafkaHeaderMapper mapper() {
        return new DefaultKafkaHeaderMapper();
    }
    private KafkaProducerMessageHandlerSpec<String, String, ?> kafkaMessageHandler(
            ProducerFactory<String, String> producerFactory, String topic) {
        return Kafka
                .outboundChannelAdapter(producerFactory)
                .messageKey(m -> m
                        .getHeaders()
                        .get(IntegrationMessageHeaderAccessor.SEQUENCE_NUMBER))
                .headerMapper(mapper())
                .partitionId(m -> 10)
                .topicExpression("headers[kafka_topic] ?: '" + topic + "'")
                .configureKafkaTemplate(t -> t.id("kafkaTemplate:" + topic));
    }


    @Bean("sendToKafkaFlow")
    public IntegrationFlow sendToKafkaFlow() {
       return IntegrationFlow.from(toKafkaMessageChannel())
                .handle(Kafka.outboundChannelAdapter(kafkaTemplate).topic("kRequests"))
                .get();
    }

   /* @Bean("receiveFromKafkaFlow")
    public IntegrationFlow receiveFromKafkaFlow(ConsumerFactory<String, String> consumerFactory){
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,kafkaConfig.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "siTestGroup");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, 100);
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 15000);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        ConsumerProperties consumerProperties=new ConsumerProperties("kReplies");
        consumerProperties.setKafkaConsumerProperties(props);
        return IntegrationFlow.from(Kafka.inboundChannelAdapter(consumerFactory,consumerProperties))
                .channel(fromKafkaMessageChannel())
                .handle((payload, headers) -> {
                    System.out.println(payload);
                    return payload;
                })
                .get();
    }*/

    public String convertToJson(Transaction transaction){
        try {
            return objectMapper.writeValueAsString(transaction);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
