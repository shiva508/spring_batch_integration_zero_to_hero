package com.pool.config.batch.tasklet;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@EnableBatchProcessing
@Configuration
@Slf4j
@AllArgsConstructor
public class TaskletLateBindingBatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean("taskletLateBinding")
    public Step taskletLateBindingStep(){
        return new StepBuilder("taskletLateBinding",jobRepository)
                .tasklet(taskletLateBindingBean(null),transactionManager)
                .build();
    }
    @Bean("taskletLateBindingJob")
    public Job taskletLateBindingJob(){
        return new JobBuilder("taskletLateBindingJob",jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(taskletLateBindingStep())
                .build();
    }

    @Bean
    @StepScope
    public Tasklet taskletLateBindingBean(@Value("#{jobParameters['jobTriggeredDate']}") String date){
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                log.info("jobTriggeredDate={}",date);
                return RepeatStatus.FINISHED;
            }
        };
    }
}
