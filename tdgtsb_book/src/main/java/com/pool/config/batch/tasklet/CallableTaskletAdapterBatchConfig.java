package com.pool.config.batch.tasklet;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.CallableTaskletAdapter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.concurrent.Callable;

@EnableBatchProcessing
@Configuration
@Slf4j
@AllArgsConstructor
public class CallableTaskletAdapterBatchConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean("callableJob")
    public Job  callableJob(){
        return new JobBuilder("callableJob",jobRepository)
                .start(callableStep())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean("callableStep")
    public Step callableStep(){
        return new StepBuilder("callableStep",jobRepository)
                .tasklet(callableTasklet(),transactionManager)
                .build();
    }

    @Bean
    public Callable<RepeatStatus> callableObject(){
        return ()->{
            log.info("Batch callable method");
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public CallableTaskletAdapter callableTasklet(){
        CallableTaskletAdapter taskletAdapter=new CallableTaskletAdapter();
        taskletAdapter.setCallable(callableObject());
        return taskletAdapter;
    }
}
