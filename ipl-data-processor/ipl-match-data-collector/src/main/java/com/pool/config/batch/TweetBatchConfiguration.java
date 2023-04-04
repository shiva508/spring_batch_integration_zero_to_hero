package com.pool.config.batch;

import com.pool.record.Tweet;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
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

//@Configuration
public class TweetBatchConfiguration {

    private final JobRepository jobRepository;

    private final PlatformTransactionManager transactionManager;

    public TweetBatchConfiguration(JobRepository jobRepository,
                                   PlatformTransactionManager transactionManager) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
    }

    @Bean
    public Job tweetJob(@Qualifier("tweetStep") Step tweetStep) {
        return new JobBuilder("tweetJob",jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(tweetStep)
                .build();
    }

    @Bean("tweetStep")
    public Step tweetStep(@Qualifier("tweetItemReader") ItemReader<Tweet> tweetItemReader,
                          @Qualifier("tweet2taskExecutor") TaskExecutor tweet2taskExecutor){
        return new StepBuilder("tweetStep",jobRepository)
                .<Tweet,Tweet>chunk(500,transactionManager)
                .reader(tweetItemReader)
                .writer(chunk -> chunk.getItems().forEach(System.out::println))
                .taskExecutor(tweet2taskExecutor)
                .build();
    }

    @Bean("tweetItemReader")
    public FlatFileItemReader<Tweet> tweetItemReader(@Value("${tweet.csv.file.path}")Resource resource){
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
}
