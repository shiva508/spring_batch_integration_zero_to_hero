package com.pool.configuration.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.pool.configuration.batch.listener.FirstJobListener;
import com.pool.configuration.batch.listener.FirstStepExecutionListener;
import com.pool.service.batch.CustomTasklet;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
public class TaskletOrientedImageBatchConfig {

	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;

	private final CustomTasklet customTasklet;

	private final FirstJobListener firstJonListener;

	private final FirstStepExecutionListener firstStepExecutionListener;

	public TaskletOrientedImageBatchConfig(JobRepository jobRepository,
										   PlatformTransactionManager transactionManager,
										   CustomTasklet customTasklet,
										   FirstJobListener firstJonListener,
										   FirstStepExecutionListener firstStepExecutionListener) {
		this.jobRepository = jobRepository;
		this.transactionManager = transactionManager;
		this.customTasklet = customTasklet;
		this.firstJonListener = firstJonListener;
		this.firstStepExecutionListener = firstStepExecutionListener;
	}

	@Bean
	public Job firstJob() {
		return new JobBuilder("firstJob",jobRepository)
								.incrementer(new RunIdIncrementer())
								.start(firstStep())
								.next(secondStep())
								.next(customStep())
								.listener(firstJonListener)
								.build();
	}

	@Bean
	public Step firstStep() {

		return new StepBuilder("firstStep",jobRepository)
				                .tasklet(firstTask(),transactionManager)
				                .listener(firstStepExecutionListener)
				                .build();
	}

	@Bean
	public Tasklet firstTask() {
		return (contribution, chunkContext) -> {
			System.out.println("First Task is completed");
			return RepeatStatus.FINISHED;
		};
	}
	

	@Bean
	public Step secondStep() {
		return new StepBuilder("secondStep",jobRepository)
				.tasklet(secondTask(),transactionManager)
				.build();
	}

	@Bean
	public Tasklet secondTask() {
		return (contribution, chunkContext) -> {
			System.out.println("Second Task is completed");
			return RepeatStatus.FINISHED;
		};
	}

	@Bean
	public Step customStep() {
		return new StepBuilder("customStep",jobRepository)
				.tasklet(customTasklet,transactionManager)
				.build();
	}
}
