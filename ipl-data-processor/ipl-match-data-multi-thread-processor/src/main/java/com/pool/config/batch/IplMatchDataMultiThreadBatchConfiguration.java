package com.pool.config.batch;

import com.pool.config.props.TweetCsvConfiguration;
import com.pool.record.Tweet;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Date;

@Configuration
public class IplMatchDataMultiThreadBatchConfiguration {

    private final JobRepository jobRepository;

    private final PlatformTransactionManager transactionManager;

    private final TweetCsvConfiguration tweetCsvConfiguration;

    public static Date date=new Date();

    public IplMatchDataMultiThreadBatchConfiguration(JobRepository jobRepository,
                                                     PlatformTransactionManager transactionManager,
                                                     TweetCsvConfiguration tweetCsvConfiguration) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.tweetCsvConfiguration=tweetCsvConfiguration;
    }

    @Bean
    public Job iplJob(@Qualifier("iplFileReader")Step iplFileReader){
        return new JobBuilder("iplJob"+date.toString(),jobRepository)
                .start(iplFileReader)
                .build();
    }

    @Bean("iplFileReader")
    public Step iplFileReader(@Qualifier("tweetFlatFileItemReader")ItemReader<Tweet> tweetFlatFileItemReader){
        ThreadPoolTaskExecutor poolTaskExecutor=new ThreadPoolTaskExecutor();
        poolTaskExecutor.setCorePoolSize(8);
        poolTaskExecutor.setMaxPoolSize(8);
        poolTaskExecutor.afterPropertiesSet();
        TaskExecutor taskExecutor=new SimpleAsyncTaskExecutor("spring_batch"+date.toString());
        return new StepBuilder("iplFileReader"+date.toString(),jobRepository)
                .<Tweet,Tweet>chunk(500,transactionManager)
                .reader(tweetFlatFileItemReader)
                .writer(chunk -> chunk.getItems().forEach(System.out::println))
                //.taskExecutor(poolTaskExecutor)
                .taskExecutor(taskExecutor)
                .build();
    }

    @Bean("tweetFlatFileItemReader")
    public FlatFileItemReader<Tweet> tweetFlatFileItemReader(@Value("${tweet.csv.file.path}")Resource resource){
        return new FlatFileItemReaderBuilder<Tweet>()
                   .name("tweetFlatFileItemReader"+date.toString())
                   .resource(resource)
                   .linesToSkip(1)
                   .delimited().delimiter(",")
                   .names(tweetCsvConfiguration.getColumns().split(","))
                   .fieldSetMapper(fieldSet -> new Tweet(fieldSet.readDate("date"),fieldSet.readString("content")))
                   .build();
    }
}
