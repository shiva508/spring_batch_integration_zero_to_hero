package com.pool;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Date;

@SpringBootApplication
public class HboMaxMoviesApplication implements CommandLineRunner {

    private final JobLauncher jobLauncher;
    private final Job hboTitleJob;

    public HboMaxMoviesApplication(JobLauncher jobLauncher,@Qualifier("hboTitleJob") Job hboTitleJob) {
        this.jobLauncher=jobLauncher;
        this.hboTitleJob = hboTitleJob;
    }

    public static void main(String[] args) {
        SpringApplication.run(HboMaxMoviesApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        jobLauncher.run(hboTitleJob,new JobParametersBuilder()
                .addDate("iplScheduleSummaryJob",new Date())
                .addString("username","dasari508")
                .addString("csvFilePath","/home/shiva/shiva/mywork/spring_batch_zero_to_hero/src/main/resources/output/students.csv")


                .toJobParameters());
    }
}
