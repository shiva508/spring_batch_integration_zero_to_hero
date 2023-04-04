package com.pool.config.batch.executioncontext;

import com.pool.config.tasklet.ExecutionContextEndTasklet;
import com.pool.config.tasklet.ExecutionContextStartTasklet;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.listener.ExecutionContextPromotionListener;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@EnableBatchProcessing
@Configuration
@Slf4j
@AllArgsConstructor
public class ExecutionContextBatchConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean("executionContextStartStep")
    public Step executionContextStartStep(){
        return new StepBuilder("executionContextStartStep",jobRepository)
                .tasklet(new ExecutionContextStartTasklet(),transactionManager)
                .listener(promotionListener())
                .build();
    }

    @Bean("executionContextEndStep")
    public Step executionContextEndStep(){
        return new StepBuilder("executionContextEndStep",jobRepository)
                .tasklet(new ExecutionContextEndTasklet(),transactionManager)
                .build();
    }

    @Bean("executionContextJob")
    public Job executionContextJob(){
        return new JobBuilder("executionContextJob",jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(executionContextStartStep())
                .next(executionContextEndStep())
                .build();
    }

    @Bean
    public ExecutionContextPromotionListener promotionListener(){
        ExecutionContextPromotionListener promotionListener=new ExecutionContextPromotionListener();
        promotionListener.setKeys(new String[]{"usernamelength"});
        return promotionListener;
    }
}
