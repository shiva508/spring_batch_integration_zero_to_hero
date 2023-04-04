package com.pool.config.batch.stepflow.decider;

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
public class StepFlowDiciderBatchConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;


    @Bean("examinationDeciderjob")
    public Job examinationDeciderjob(){
        return new JobBuilder("examinationDeciderjob",jobRepository)
                .start(writeExamDeciderStep())
                .next(jobExecutionDecider())
                .from(jobExecutionDecider())
                .on("FAILED").to(onFailureDeciderStep())
                .from(jobExecutionDecider())
                .on("*").to(onQualifyDeciderStep())
                .end()
                .build();
    }

    /*=================Step======================*/
    @Bean("writeExamDeciderStep")
    public Step writeExamDeciderStep(){
        return new StepBuilder("writeExamDeciderStep",jobRepository)
                .tasklet(writeExamDeciderTasklet(),transactionManager)
                .build();
    }
    @Bean("onQualifyDeciderStep")
    public Step onQualifyDeciderStep(){
        return new StepBuilder("onQualifyDeciderStep",jobRepository)
                .tasklet(onQualifyDeciderTasklet(),transactionManager)
                .build();
    }

    @Bean("onFailureDeciderStep")
    public Step onFailureDeciderStep(){
        return new StepBuilder("onFailureDeciderStep",jobRepository)
                .tasklet(onFailureDeciderTasklet(),transactionManager)
                .build();
    }
    /*=================TASKLETS======================*/

    @Bean("writeExamDeciderTasklet")
    public Tasklet writeExamDeciderTasklet(){
        return (contribution, chunkContext) -> {
            log.info("writeExamTasklet=======================>:{}",chunkContext.getStepContext().getStepName());
            return RepeatStatus.FINISHED;
            //throw new RuntimeException("Something went really wrong!!!!!!!!!!");
        };
    }

    @Bean("onQualifyDeciderTasklet")
    public Tasklet onQualifyDeciderTasklet(){
        return (contribution, chunkContext) -> {
            log.info("onSuccessTasklet========================>:{}",chunkContext.getStepContext().getStepName());
            return RepeatStatus.FINISHED;
        };
    }

    @Bean("onFailureDeciderTasklet")
    public Tasklet onFailureDeciderTasklet(){
        return (contribution, chunkContext) -> {
            log.info("onFailureTasklet=========================>:{}",chunkContext.getStepContext().getStepName());
            return RepeatStatus.FINISHED;
        };
    }

    /*======================>:-:DECIDER:-:<====================*/

    @Bean("jobExecutionDecider")
    public JobExecutionDecider jobExecutionDecider(){
        return new CustomJobExecutionDecider();
    }
}
