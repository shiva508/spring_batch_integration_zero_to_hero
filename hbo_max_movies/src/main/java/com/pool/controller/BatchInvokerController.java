package com.pool.controller;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Date;

@RestController
public class BatchInvokerController {
    private final JobLauncher jobLauncher;
    private final Job hboTitleMultithreadJob;

    public BatchInvokerController(JobLauncher jobLauncher,@Qualifier("hboTitleMultithreadJob") Job hboTitleMultithreadJob) {
        this.jobLauncher=jobLauncher;
        this.hboTitleMultithreadJob = hboTitleMultithreadJob;
    }

    @GetMapping("/invokejob")
    public String invokeBatch() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        jobLauncher.run(hboTitleMultithreadJob,new JobParametersBuilder()
                .addDate("iplScheduleSummaryJob",new Date())
                .addString("username","dasari508")
                .addString("csvFilePath","/home/shiva/shiva/mywork/spring_batch_zero_to_hero/src/main/resources/output/students.csv")
                .toJobParameters());
        return "Ok";
    }
}
