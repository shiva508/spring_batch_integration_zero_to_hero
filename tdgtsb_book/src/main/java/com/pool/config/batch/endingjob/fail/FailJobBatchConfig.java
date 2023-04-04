package com.pool.config.batch.endingjob.fail;

import com.pool.config.decider.CustomJobExecutionDecider;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
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
public class FailJobBatchConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean("examinationFailJob")
    public Job examinationFailJob(){
        return new JobBuilder("examinationFailJob",jobRepository)
                .start(writeExamFailJobStep())
                .on("FAILED").fail()
                .from(writeExamFailJobStep()).on("*").to(onQualifyFailJobStep())
                .end()
                .build();
    }

    /*=================Step======================*/
    @Bean("writeExamFailJobStep")
    public Step writeExamFailJobStep(){
        return new StepBuilder("writeExamFailJobStep",jobRepository)
                .tasklet(writeExamFailJobTasklet(),transactionManager)
                .build();
    }
    @Bean("onQualifyFailJobStep")
    public Step onQualifyFailJobStep(){
        return new StepBuilder("onQualifyFailJobStep",jobRepository)
                .tasklet(onQualifyFailJobTasklet(),transactionManager)
                .build();
    }

    @Bean("onFailureFailJobStep")
    public Step onFailureFailJobStep(){
        return new StepBuilder("onFailureFailJobStep",jobRepository)
                .tasklet(onFailureFailJobTasklet(),transactionManager)
                .build();
    }
    /*=================TASKLETS======================*/

    @Bean("writeExamFailJobTasklet")
    public Tasklet writeExamFailJobTasklet(){
        return (contribution, chunkContext) -> {
            log.info("writeExamTasklet=======================>:{}",chunkContext.getStepContext().getStepName());
            //return RepeatStatus.FINISHED;
            throw new RuntimeException("Something went really wrong!!!!!!!!!!");
        };
    }

    @Bean("onQualifyFailJobTasklet")
    public Tasklet onQualifyFailJobTasklet(){
        return (contribution, chunkContext) -> {
            log.info("onSuccessTasklet========================>:{}",chunkContext.getStepContext().getStepName());
            return RepeatStatus.FINISHED;
        };
    }

    @Bean("onFailureFailJobTasklet")
    public Tasklet onFailureFailJobTasklet(){
        return (contribution, chunkContext) -> {
            log.info("onFailureTasklet=========================>:{}",chunkContext.getStepContext().getStepName());
            return RepeatStatus.FINISHED;
        };
    }

    /*======================>:-:DECIDER:-:<====================*/

    @Bean("jobExecutionFailJobDecider")
    public JobExecutionDecider jobExecutionFailJobDecider(){
        return new CustomJobExecutionDecider();
    }

}
