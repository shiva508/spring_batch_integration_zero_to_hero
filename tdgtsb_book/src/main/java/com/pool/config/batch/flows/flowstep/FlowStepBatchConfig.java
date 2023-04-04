package com.pool.config.batch.flows.flowstep;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
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
public class FlowStepBatchConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    /*=================TASKLETS======================*/

    @Bean("loadIplScheduleFlowStep")
    public Tasklet loadIplScheduleFlowStep(){
        return (contribution, chunkContext) -> {
            log.info("loadIplSchedule========================>:{}",chunkContext.getStepContext().getStepName());
            return RepeatStatus.FINISHED;
        };
    }
    @Bean("loadIciWordCupScheduleFlowStep")
    public Tasklet loadIccWordCupScheduleFlowStep(){
        return (contribution, chunkContext) -> {
            log.info("loadIciWordCupSchedule========================>:{}",chunkContext.getStepContext().getStepName());
            return RepeatStatus.FINISHED;
        };
    }
    @Bean("startUpdateIccScheduleFlowStep")
    public Tasklet startUpdateIccScheduleFlowStep(){
        return (contribution, chunkContext) -> {
            log.info("updateIccSchedule========================>:{}",chunkContext.getStepContext().getStepName());
            return RepeatStatus.FINISHED;
        };
    }

    @Bean("runScheduleBatchUpdateFlowStep")
    public Tasklet runScheduleBatchUpdateFlowStep(){
        return (contribution, chunkContext) -> {
            log.info("runScheduleBatchUpdate========================>:{}",chunkContext.getStepContext().getStepName());
            return RepeatStatus.FINISHED;
        };
    }

    /*=================STEPS======================*/

    @Bean("loadIplScheduleStepFlowStep")
    public Step loadIplScheduleStepFlowStep(){
        return new StepBuilder("loadIplScheduleStepFlowStep",jobRepository)
                .tasklet(loadIplScheduleFlowStep(),transactionManager)
                .build();
    }

    @Bean("loadIccWordCupScheduleStepFlowStep")
    public Step loadIccWordCupScheduleStepFlowStep(){
        return new StepBuilder("loadIccWordCupScheduleStepFlowStep",jobRepository)
                .tasklet(loadIccWordCupScheduleFlowStep(),transactionManager)
                .build();
    }

    @Bean("startUpdateIccScheduleStepFlowStep")
    public Step startUpdateIccScheduleStepFlowStep(){
        return new StepBuilder("startUpdateIccScheduleStepFlowStep",jobRepository)
                .tasklet(startUpdateIccScheduleFlowStep(),transactionManager)
                .build();
    }

    @Bean("runScheduleBatchUpdateStepFlowStep")
    public Step runScheduleBatchUpdateStepFlowStep(){
        return new StepBuilder("runScheduleBatchUpdateFlowStep",jobRepository)
                .tasklet(runScheduleBatchUpdateFlowStep(),transactionManager)
                .build();
    }
    /*=================FLOW======================*/
    @Bean("cricketScheduleFlowFlowStep")
    public Flow cricketScheduleFlowFlowStep(){
        return new FlowBuilder<Flow>("cricketScheduleFlowFlowStep")
                .start(loadIplScheduleStepFlowStep())
                .next(loadIccWordCupScheduleStepFlowStep())
                .next(startUpdateIccScheduleStepFlowStep())
                .build();
    }


    /*=================STEP THAT INITIALIZE START FLOW======================*/

    @Bean("initializeIplScheduleFlowStep")
    public Step initializeIplScheduleFlowStep(){
        return new StepBuilder("initializeIplScheduleFlowStep",jobRepository)
                .flow(cricketScheduleFlowFlowStep())
                .build();
    }

    /*=================JOB THAT PICKS FLOW STEP AND NORMAL STEP======================*/
    @Bean("iplScheduleSummaryJobFlowStep")
    public Job iplScheduleSummaryJobFlowStep(){
        return new JobBuilder("iplScheduleSummaryJobFlowStep",jobRepository)
                .start(cricketScheduleFlowFlowStep())
                .next(runScheduleBatchUpdateStepFlowStep())
                .end().build();
    }
}
