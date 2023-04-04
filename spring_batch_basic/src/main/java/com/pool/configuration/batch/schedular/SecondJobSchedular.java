package com.pool.configuration.batch.schedular;

import java.util.Date;

import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class SecondJobSchedular {
	@Autowired
	private JobLauncher jobLauncher;

	@Qualifier("firstJob")
	@Autowired
	private Job firstJob;

	//@Scheduled(cron = "${secondjob.run.rate}")
	public void runSecondJob() {
		try {
			JobExecution jobExecution  = jobLauncher.run(firstJob, new JobParametersBuilder().addDate("Time",new Date()).toJobParameters());
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		

	}
}
