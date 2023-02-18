package com.pool.config;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class EndStepConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    public EndStepConfiguration(JobRepository jobRepository,
                                PlatformTransactionManager transactionManager) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
    }

    @Bean
    public Step jobFinidhedStep(){
        return new StepBuilder("jobFinidhedStep",jobRepository)
                .tasklet((stepContribution, chunkContext) -> {
                    System.out.println("Job finished");
                    return RepeatStatus.FINISHED;
                },transactionManager)
                .build();
    }
}
