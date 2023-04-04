package com.pool.config.batch.flows.externalize;


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
public class ExternalizeFlowBatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    /*=================TASKLETS======================*/

    @Bean("loadIplSchedule")
    public Tasklet loadIplSchedule(){
        return (contribution, chunkContext) -> {
            log.info("loadIplSchedule========================>:{}",chunkContext.getStepContext().getStepName());
            return RepeatStatus.FINISHED;
        };
    }
    @Bean("loadIciWordCupSchedule")
    public Tasklet loadIccWordCupSchedule(){
        return (contribution, chunkContext) -> {
            log.info("loadIciWordCupSchedule========================>:{}",chunkContext.getStepContext().getStepName());
            return RepeatStatus.FINISHED;
        };
    }
    @Bean("startUpdateIccSchedule")
    public Tasklet startUpdateIccSchedule(){
        return (contribution, chunkContext) -> {
            log.info("updateIccSchedule========================>:{}",chunkContext.getStepContext().getStepName());
            return RepeatStatus.FINISHED;
        };
    }

    @Bean("runScheduleBatchUpdate")
    public Tasklet runScheduleBatchUpdate(){
        return (contribution, chunkContext) -> {
            log.info("runScheduleBatchUpdate========================>:{}",chunkContext.getStepContext().getStepName());
            return RepeatStatus.FINISHED;
        };
    }

    /*=================STEPS======================*/

    @Bean("loadIplScheduleStep")
    public Step  loadIplScheduleStep(){
        return new StepBuilder("loadIplScheduleStep",jobRepository)
                .tasklet(loadIplSchedule(),transactionManager)
                .build();
    }

    @Bean("loadIccWordCupScheduleStep")
    public Step loadIccWordCupScheduleStep(){
        return new StepBuilder("loadIccWordCupScheduleStep",jobRepository)
                .tasklet(loadIccWordCupSchedule(),transactionManager)
                .build();
    }

    @Bean("startUpdateIccScheduleStep")
    public Step startUpdateIccScheduleStep(){
        return new StepBuilder("startUpdateIccScheduleStep",jobRepository)
                .tasklet(startUpdateIccSchedule(),transactionManager)
                .build();
    }

    @Bean("runScheduleBatchUpdateStep")
    public Step runScheduleBatchUpdateStep(){
        return new StepBuilder("runScheduleBatchUpdate",jobRepository)
                .tasklet(runScheduleBatchUpdate(),transactionManager)
                .build();
    }

    /*=================FLOW======================*/

    @Bean("cricketScheduleFlow")
    public Flow cricketScheduleFlow(){
        return new FlowBuilder<Flow>("cricketScheduleFlow")
                .start(loadIplScheduleStep())
                .next(loadIccWordCupScheduleStep())
                .next(startUpdateIccScheduleStep())
                .build();
    }

    /*=================JOB======================*/

    @Bean("iplScheduleSummaryJob")
    public Job iplScheduleSummaryJob(){
        return new JobBuilder("iplScheduleSummaryJob",jobRepository)
                .start(cricketScheduleFlow())
                .next(runScheduleBatchUpdateStep())
                .end().build();
    }

}
