package com.pool.config.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@EnableBatchProcessing
@Configuration
@Slf4j
public class BasicBatchConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    public BasicBatchConfig(JobRepository jobRepository,
                            PlatformTransactionManager transactionManager) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
    }

    @Bean("basicStep")
    public Step basicStep(){
        return new StepBuilder("basicStep",jobRepository)
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                        log.info("job name={}",chunkContext.getStepContext().getJobName());
                        chunkContext.getStepContext().getJobParameters().entrySet().forEach(System.out::println);
                        return RepeatStatus.FINISHED;
                    }
                },transactionManager)
                .build();
    }
    @Bean("basicJob")
    public Job basicJob(){
        return new JobBuilder("basicJob",jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(basicStep())
                .build();
    }
}
