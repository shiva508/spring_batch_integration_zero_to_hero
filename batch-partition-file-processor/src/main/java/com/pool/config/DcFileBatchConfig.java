package com.pool.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class DcFileBatchConfig {

    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private PlatformTransactionManager platformTransactionManager;

    @Bean
    public Step basicStep(){
        return new StepBuilder("basicStep",jobRepository).tasklet(basicTaskLet(),platformTransactionManager).build();
    }

    @Bean("basicStep")
    public Job basicJob(){
        return new JobBuilder("basicJob",jobRepository).start(basicStep()).incrementer(new RunIdIncrementer()).build();
    }

    @Bean
    public Tasklet basicTaskLet(){
        return (contribution, chunkContext) -> {
            System.out.println("O=======================K");
            return RepeatStatus.FINISHED;
        };
    }
}
