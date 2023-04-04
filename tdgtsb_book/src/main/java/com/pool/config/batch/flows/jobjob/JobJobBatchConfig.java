package com.pool.config.batch.flows.jobjob;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.job.DefaultJobParametersExtractor;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@EnableBatchProcessing
@Configuration
@Slf4j
@AllArgsConstructor
public class JobJobBatchConfig {

    private final JobRepository jobRepository;

    private final PlatformTransactionManager transactionManager;

    /*=================TASKLETS======================*/
    @Bean("loadIplScheduleJobJob")
    public Tasklet loadIplScheduleJobJob(){
        return (contribution, chunkContext) -> {
            log.info("loadIplSchedule========================>:{}",chunkContext.getStepContext().getStepName());
            return RepeatStatus.FINISHED;
        };
    }
    @Bean("loadIciWordCupScheduleJobJob")
    public Tasklet loadIccWordCupScheduleJobJob(){
        return (contribution, chunkContext) -> {
            log.info("loadIciWordCupSchedule========================>:{}",chunkContext.getStepContext().getStepName());
            return RepeatStatus.FINISHED;
        };
    }
    @Bean("startUpdateIccScheduleJobJob")
    public Tasklet startUpdateIccScheduleJobJob(){
        return (contribution, chunkContext) -> {
            log.info("updateIccSchedule========================>:{}",chunkContext.getStepContext().getStepName());
            return RepeatStatus.FINISHED;
        };
    }

    @Bean("runScheduleBatchUpdateJobJob")
    public Tasklet runScheduleBatchUpdateJobJob(){
        return (contribution, chunkContext) -> {
            log.info("runScheduleBatchUpdate========================>:{}",chunkContext.getStepContext().getStepName());
            return RepeatStatus.FINISHED;
        };
    }

    /*=================STEPS======================*/

    @Bean("loadIplScheduleStepJobJob")
    public Step loadIplScheduleStepJobJob(){
        return new StepBuilder("loadIplScheduleStepJobJob",jobRepository)
                .tasklet(loadIplScheduleJobJob(),transactionManager)
                .build();
    }

    @Bean("loadIccWordCupScheduleStepJobJob")
    public Step loadIccWordCupScheduleStepJobJob(){
        return new StepBuilder("loadIccWordCupScheduleStepJobJob",jobRepository)
                .tasklet(loadIccWordCupScheduleJobJob(),transactionManager)
                .build();
    }

    @Bean("startUpdateIccScheduleStepJobJob")
    public Step startUpdateIccScheduleStepJobJob(){
        return new StepBuilder("startUpdateIccScheduleStepJobJob",jobRepository)
                .tasklet(startUpdateIccScheduleJobJob(),transactionManager)
                .build();
    }

    @Bean("runScheduleBatchUpdateStepJobJob")
    public Step runScheduleBatchUpdateStepJobJob(){
        return new StepBuilder("runScheduleBatchUpdateJobJob",jobRepository)
                .tasklet(runScheduleBatchUpdateJobJob(),transactionManager)
                .build();
    }
    /*=================JOB THAT REPLACES FLOW======================*/

    @Bean("cricketScheduleJob")
    public Job cricketScheduleJob(){
        return new JobBuilder("cricketScheduleJob",jobRepository)
                .start(loadIplScheduleStepJobJob())
                .next(loadIccWordCupScheduleStepJobJob())
                .next(startUpdateIccScheduleStepJobJob())
                .build();
    }

    /*=================JOB THAT RUNS ANOTHER JOB ======================*/

    @Bean("jobInvokerStep")
    public Step jobInvokerStep(){
        return new StepBuilder("jobInvokerStep",jobRepository)
                .job(cricketScheduleJob())
                .parametersExtractor(new DefaultJobParametersExtractor())
                .build();
    }

    @Bean("iplScheduleSummaryJobJob")
    public Job iplScheduleSummaryJobJob(){
        return new JobBuilder("iplScheduleSummaryJobJob",jobRepository)
                .start(jobInvokerStep())
                .next(runScheduleBatchUpdateStepJobJob())
                .build();
    }
}
