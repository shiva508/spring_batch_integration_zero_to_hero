package com.pool;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportRuntimeHints;

import java.util.Date;

@SpringBootApplication
@ComponentScan(basePackages = "com.pool")
public class LeaderApplication {
    public static void main(String[] args) {
        SpringApplication.run(LeaderApplication.class, args);
    }
    @Bean
    public ApplicationRunner applicationRunner(JobLauncher jobLauncher, @Qualifier("videoGameJob") Job job){
        return args -> {
            var jobParameters=new JobParametersBuilder()
                    //.addString("uuid", UUID.randomUUID().toString())
                    .addDate("date",new Date())
                    //.addString("date",key)
                    .toJobParameters();
            JobExecution run = jobLauncher.run(job, jobParameters);
            long instanceId = run.getJobInstance().getInstanceId();
            System.out.println("Batch run id:"+instanceId);
        };
    }
}