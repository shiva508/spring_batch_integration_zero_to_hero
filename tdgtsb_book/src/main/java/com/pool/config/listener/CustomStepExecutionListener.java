package com.pool.config.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

@Slf4j
public class CustomStepExecutionListener implements StepExecutionListener {
    @Override
    public void beforeStep(StepExecution stepExecution) {
        log.info("beforeStep============{},{}",stepExecution.getStepName(),stepExecution.getStatus());
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        log.info("afterStep============{},{}",stepExecution.getStepName(),stepExecution.getStatus());
        return StepExecutionListener.super.afterStep(stepExecution);
    }
}
