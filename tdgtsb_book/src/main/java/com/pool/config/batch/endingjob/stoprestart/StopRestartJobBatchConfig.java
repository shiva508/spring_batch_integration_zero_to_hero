package com.pool.config.batch.endingjob.stoprestart;

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
public class StopRestartJobBatchConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean("examinationStopRestartlJob")
    public Job examinationStopRestartlJob(){
        return new JobBuilder("examinationStopRestartlJob",jobRepository)
                .start(writeExamStopRestartJobStep())
                .on("FAILED").stopAndRestart(onQualifyStopRestartJobStep())
                .from(writeExamStopRestartJobStep()).on("*").to(onQualifyStopRestartJobStep())
                .end()
                .build();
    }

    /*=================Step======================*/
    @Bean("writeExamStopRestartJobStep")
    public Step writeExamStopRestartJobStep(){
        return new StepBuilder("writeExamStopRestartJobStep",jobRepository)
                .tasklet(writeExamStopRestartJobTasklet(),transactionManager)
                .build();
    }
    @Bean("onQualifyStopRestartJobStep")
    public Step onQualifyStopRestartJobStep(){
        return new StepBuilder("onQualifyStopRestartJobStep",jobRepository)
                .tasklet(onQualifyStopRestartJobTasklet(),transactionManager)
                .build();
    }

    @Bean("onFailureStopRestartJobStep")
    public Step onFailureStopRestartJobStep(){
        return new StepBuilder("onFailureStopRestartJobStep",jobRepository)
                .tasklet(onFailureStopRestartJobTasklet(),transactionManager)
                .build();
    }
    /*=================TASKLETS======================*/

    @Bean("writeExamStopRestartJobTasklet")
    public Tasklet writeExamStopRestartJobTasklet(){
        return (contribution, chunkContext) -> {
            log.info("writeExamTasklet=======================>:{}",chunkContext.getStepContext().getStepName());
            //return RepeatStatus.FINISHED;
            throw new RuntimeException("Something went really wrong!!!!!!!!!!");
        };
    }

    @Bean("onQualifyStopRestartJobTasklet")
    public Tasklet onQualifyStopRestartJobTasklet(){
        return (contribution, chunkContext) -> {
            log.info("onSuccessTasklet========================>:{}",chunkContext.getStepContext().getStepName());
            return RepeatStatus.FINISHED;
        };
    }

    @Bean("onFailureStopRestartJobTasklet")
    public Tasklet onFailureStopRestartJobTasklet(){
        return (contribution, chunkContext) -> {
            log.info("onFailureTasklet=========================>:{}",chunkContext.getStepContext().getStepName());
            return RepeatStatus.FINISHED;
        };
    }

    /*======================>:-:DECIDER:-:<====================*/

    @Bean("jobExecutionStopRestartJobDecider")
    public JobExecutionDecider jobExecutionStopRestartJobDecider(){
        return new CustomJobExecutionDecider();
    }

}
