package com.pool.config.tasklet.jobmetadata;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;
import java.util.List;
@Slf4j
@Component
public class JobMetadataStartTasklet implements Tasklet {

    private final JobExplorer jobExplorer;
    public JobMetadataStartTasklet(JobExplorer jobExplorer) {
        this.jobExplorer = jobExplorer;
    }
    private StepExecution stepExecution;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        var jobName = chunkContext.getStepContext().getJobName();
        var stepName = chunkContext.getStepContext().getStepName();
        log.info("JOB NAME:-: "+jobName);
        log.info("STEP NAME:-: "+stepName);

        List<JobInstance> jobInstances =jobExplorer.getJobInstances(jobName,0,Integer.MAX_VALUE);
        log.info("JOB INSTANCE SIZE {},JOB NAME {}",jobInstances.size(),jobName);
        chunkContext.getStepContext()
                    .getStepExecution()
                    .getJobExecution()
                    .getExecutionContext().put("JOB_ID","SHIVA");
        return RepeatStatus.FINISHED;
    }


}
