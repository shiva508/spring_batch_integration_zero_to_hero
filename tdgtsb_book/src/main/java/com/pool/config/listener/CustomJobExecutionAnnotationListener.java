package com.pool.config.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.annotation.AfterJob;
import org.springframework.batch.core.annotation.BeforeJob;

@Slf4j
public class CustomJobExecutionAnnotationListener {

    @BeforeJob
    public void beforeJob(JobExecution jobExecution) {
        log.info("beforeJob============:{}",jobExecution.getJobInstance().getJobName());
    }
    @AfterJob
    public void afterJob(JobExecution jobExecution) {
        log.info("afterJob=================:{}",jobExecution.getStatus());
    }
}
