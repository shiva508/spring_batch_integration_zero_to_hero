package com.pool.configuration.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.pool.configuration.batch.listener.FirstJobListener;
import com.pool.configuration.batch.listener.FirstStepExecutionListener;
import com.pool.service.batch.CustomTasklet;

@Configuration
@EnableBatchProcessing
public class TaskletOrientedImageBatchConfig {
	
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	@Autowired
	private CustomTasklet customTasklet;
	
	@Autowired
	private FirstJobListener firstJonListener;
	
	@Autowired
	private FirstStepExecutionListener firstStepExecutionListener;
	
	
	@Bean
	public Job firstJob() {
		return jobBuilderFactory
								.get("First Job")
								.incrementer(new RunIdIncrementer())
								.start(firstStep())
								.next(secondStep())
								.next(customStep())
								.listener(firstJonListener)
								.build();
	}
	
	private Step firstStep() {
		return stepBuilderFactory
				                .get("First Step")
				                .tasklet(firstTask())
				                .listener(firstStepExecutionListener)
				                .build();
	}
	
	private Tasklet firstTask() {
		return new Tasklet() {
			
			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
				System.out.println("First Task is completed");
				return RepeatStatus.FINISHED;
			}
		};
	}
	
	
	private Step secondStep() {
		return stepBuilderFactory.get("second Step").tasklet(secondTask()).build();
	}
	
	private Tasklet secondTask() {
		return new Tasklet() {
			
			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
				System.out.println("Second Task is completed");
				return RepeatStatus.FINISHED;
			}
		};
	}
	
	private Step customStep() {
		return stepBuilderFactory.get("Custom Task").tasklet(customTasklet).build();
	}
}
