package com.pool.config;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class ErrorStepConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    public ErrorStepConfiguration(JobRepository jobRepository,
                                  PlatformTransactionManager transactionManager) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
    }

    @Bean
    public Step errorStep(){
        return new StepBuilder("errorStep",jobRepository)
                .tasklet((stepContribution, chunkContext) -> {
                    System.out.println("Something went wrong");
                    return RepeatStatus.FINISHED;
                },transactionManager)
                .build();
    }
}
