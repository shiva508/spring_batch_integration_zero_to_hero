package com.pool.config.batch.endingjob.end;

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
public class EndingJobBatchConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean("examinationEndJob")
    public Job examinationEndJob(){
        return new JobBuilder("examinationEndJob",jobRepository)
                .start(writeExamEndJobStep())
                .on("FAILED").end()
                .from(writeExamEndJobStep())
                .on("*").to(onQualifyEndJobStep())
                .end()
                .build();
    }

    /*=================Step======================*/
    @Bean("writeExamEndJobStep")
    public Step writeExamEndJobStep(){
        return new StepBuilder("writeExamEndJobStep",jobRepository)
                .tasklet(writeExamEndJobTasklet(),transactionManager)
                .build();
    }
    @Bean("onQualifyEndJobStep")
    public Step onQualifyEndJobStep(){
        return new StepBuilder("onQualifyEndJobStep",jobRepository)
                .tasklet(onQualifyEndJobTasklet(),transactionManager)
                .build();
    }

    @Bean("onFailureEndJobStep")
    public Step onFailureEndJobStep(){
        return new StepBuilder("onFailureEndJobStep",jobRepository)
                .tasklet(onFailureEndJobTasklet(),transactionManager)
                .build();
    }
    /*=================TASKLETS======================*/

    @Bean("writeExamEndJobTasklet")
    public Tasklet writeExamEndJobTasklet(){
        return (contribution, chunkContext) -> {
            log.info("writeExamTasklet=======================>:{}",chunkContext.getStepContext().getStepName());
            //return RepeatStatus.FINISHED;
            throw new RuntimeException("Something went really wrong!!!!!!!!!!");
        };
    }

    @Bean("onQualifyEndJobTasklet")
    public Tasklet onQualifyEndJobTasklet(){
        return (contribution, chunkContext) -> {
            log.info("onSuccessTasklet========================>:{}",chunkContext.getStepContext().getStepName());
            return RepeatStatus.FINISHED;
        };
    }

    @Bean("onFailureEndJobTasklet")
    public Tasklet onFailureEndJobTasklet(){
        return (contribution, chunkContext) -> {
            log.info("onFailureTasklet=========================>:{}",chunkContext.getStepContext().getStepName());
            return RepeatStatus.FINISHED;
        };
    }

    /*======================>:-:DECIDER:-:<====================*/

    @Bean("jobExecutionEndJobDecider")
    public JobExecutionDecider jobExecutionEndJobDecider(){
        return new CustomJobExecutionDecider();
    }
}
