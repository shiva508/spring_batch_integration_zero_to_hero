package com.pool.configuration.batch.listener;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class FirstJobListener implements JobExecutionListener{

	@Override
	public void beforeJob(JobExecution jobExecution) {
		System.out.println("Before Job "+jobExecution.getJobInstance().getJobName());
		System.out.println("Before Job Param "+jobExecution.getJobParameters());
		System.out.println("Before Job context "+jobExecution.getExecutionContext());
		jobExecution.getExecutionContext().put("Shiva", "Batman");
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		System.out.println("After Job "+jobExecution.getJobInstance().getJobName());
		System.out.println("After Job Param "+jobExecution.getJobParameters());
		System.out.println("After Job context "+jobExecution.getExecutionContext());
	}

}
