package com.pool.configuration.batch.listener;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class FirstStepExecutionListener implements StepExecutionListener {

	@Override
	public void beforeStep(StepExecution stepExecution) {
		System.out.println("Before First Step :"+stepExecution.getStepName());
		System.out.println("Before First Step :"+stepExecution.getJobExecution().getExecutionContext());
		stepExecution.getExecutionContext().put("Robbin", "Rda");
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		System.out.println("After First Step :"+stepExecution.getStepName());
		System.out.println("After First Step  Context:"+stepExecution.getJobExecution().getExecutionContext());
		System.out.println("After First Step :"+stepExecution.getExecutionContext());
		return ExitStatus.COMPLETED;
	}

}
