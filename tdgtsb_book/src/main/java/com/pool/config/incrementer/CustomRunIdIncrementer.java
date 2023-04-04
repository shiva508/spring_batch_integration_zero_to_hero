package com.pool.config.incrementer;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersIncrementer;

import java.util.Date;

public class CustomRunIdIncrementer implements JobParametersIncrementer {
    @Override
    public JobParameters getNext(JobParameters parameters) {
        System.out.println("Called");
        return new JobParametersBuilder(parameters).addDate("customRunId",new Date()).toJobParameters();
    }
}
