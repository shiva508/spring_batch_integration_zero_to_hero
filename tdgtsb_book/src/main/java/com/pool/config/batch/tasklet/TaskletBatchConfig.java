package com.pool.config.batch.tasklet;

import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class TaskletBatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean("taskletStep")
    public Step taskletStep(){
        return new StepBuilder("basicStep",jobRepository)
                .tasklet(taskletBean(),transactionManager)
                .build();
    }
    @Bean("taskletJob")
    public Job taskletJob(){
        return new JobBuilder("taskletJob",jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(taskletStep())
                .build();
    }

    @Bean
    public Tasklet taskletBean(){
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                log.info("job name={}",chunkContext.getStepContext().getJobName());
                chunkContext.getStepContext().getJobParameters().entrySet().forEach(System.out::println);
                return RepeatStatus.FINISHED;
            }
        };
    }
}
