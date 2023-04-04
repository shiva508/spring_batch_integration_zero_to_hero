package com.pool.config.batch.stepflow;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
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
public class StepFlowBatchConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job examinationjob(){
        return new JobBuilder("examinationjob",jobRepository)
                .start(writeExamStep())
                .on("FAILED").to(onFailureStep())
                .from(writeExamStep()).on("*").to(onQualifyStep())
                .end().build();
    }

    /*=================Step======================*/
    @Bean("writeExamStep")
    public Step writeExamStep(){
        return new StepBuilder("writeExamStep",jobRepository)
                .tasklet(writeExamTasklet(),transactionManager)
                .build();
    }
    @Bean("onQualifyStep")
    public Step onQualifyStep(){
        return new StepBuilder("onQualifyStep",jobRepository)
                .tasklet(onQualifyTasklet(),transactionManager)
                .build();
    }

    @Bean("onFailureStep")
    public Step onFailureStep(){
        return new StepBuilder("onFailureStep",jobRepository)
                .tasklet(onFailureTasklet(),transactionManager)
                .build();
    }
    /*=================TASKLETS======================*/

    @Bean("writeExamTasklet")
    public Tasklet writeExamTasklet(){
        return (contribution, chunkContext) -> {
            log.info("writeExamTasklet=======================>:{}",chunkContext.getStepContext().getStepName());
            return RepeatStatus.FINISHED;
            //throw new RuntimeException("Something went really wrong!!!!!!!!!!");
        };
    }

    @Bean("onQualifyTasklet")
    public Tasklet onQualifyTasklet(){
        return (contribution, chunkContext) -> {
            log.info("onSuccessTasklet========================>:{}",chunkContext.getStepContext().getStepName());
            return RepeatStatus.FINISHED;
        };
    }

    @Bean("onFailureTasklet")
    public Tasklet onFailureTasklet(){
        return (contribution, chunkContext) -> {
            log.info("onFailureTasklet=========================>:{}",chunkContext.getStepContext().getStepName());
            return RepeatStatus.FINISHED;
        };
    }
}
