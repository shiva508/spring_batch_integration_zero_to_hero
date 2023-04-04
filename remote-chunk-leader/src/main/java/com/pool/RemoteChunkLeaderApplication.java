package com.pool;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Date;

@SpringBootApplication
public class RemoteChunkLeaderApplication {

    public static void main(String[] args) {
        SpringApplication.run(RemoteChunkLeaderApplication.class, args);
    }

    @Bean
    public ApplicationRunner applicationRunner(JobLauncher jobLauncher, Job job){
        return args -> {
            var jobParameters=new JobParametersBuilder()
                                            .addString("date",new Date().toString())
                                            .toJobParameters();
            JobExecution run = jobLauncher.run(job, jobParameters);
            long instanceId = run.getJobInstance().getInstanceId();
            System.out.println("Batch run id:"+instanceId);
        };
    }

}