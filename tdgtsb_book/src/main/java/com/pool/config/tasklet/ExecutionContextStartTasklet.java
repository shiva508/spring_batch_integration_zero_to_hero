package com.pool.config.tasklet;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;

@Slf4j
public class ExecutionContextStartTasklet implements Tasklet {
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
       String userName =(String)chunkContext.getStepContext().getJobParameters().get("username");
        ExecutionContext executionContext = chunkContext.getStepContext()
                                                        .getStepExecution()
                                                        .getJobExecution()
                                                        .getExecutionContext();
        log.info("userName={}",userName);
        executionContext.put("usernamelength",userName.length());
        return RepeatStatus.FINISHED;
    }
}
