package com.pool.configuration.batch.schedular;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
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
			Map<String, JobParameter> param = new HashMap<>();
			param.put("Time", new JobParameter(new Date()));
			JobParameters jobParameters = new JobParameters(param);
			JobExecution jobExecution = null;
			jobExecution = jobLauncher.run(firstJob, jobParameters);	
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		

	}
}
