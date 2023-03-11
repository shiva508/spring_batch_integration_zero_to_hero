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
import com.pool.configuration.batch.reader.SecondItemWithoutProcessorReader;
import com.pool.configuration.batch.writer.SecondItemWithoutProcessorWriter;

@Configuration
@EnableBatchProcessing
public class ChunkOrientedWithoutProcessorImageBatchConfig {

	private JobBuilderFactory jobBuilderFactory;
	private StepBuilderFactory stepBuilderFactory;

	private SecondItemWithoutProcessorReader withoutProcessorReader;

	private SecondItemWithoutProcessorWriter withoutProcessorWriter;

	@Autowired
	public ChunkOrientedWithoutProcessorImageBatchConfig(JobBuilderFactory jobBuilderFactory,
			StepBuilderFactory stepBuilderFactory, SecondItemWithoutProcessorReader withoutProcessorReader,
			SecondItemWithoutProcessorWriter withoutProcessorWriter) {
		super();
		this.jobBuilderFactory = jobBuilderFactory;
		this.stepBuilderFactory = stepBuilderFactory;
		this.withoutProcessorReader = withoutProcessorReader;
		this.withoutProcessorWriter = withoutProcessorWriter;
	}
	
	@Bean
	public Job jobWithoutProcessor() {
		return jobBuilderFactory.get("Job Without Processor")
		.incrementer(new RunIdIncrementer())
		.start(stepWithoutProcessor())
		.next(stepWithTasklet())
		.build();
	}
	
	public Step stepWithoutProcessor() {
		return stepBuilderFactory.get("Step Without Processor")
		.<Integer,Integer>chunk(3)
		.reader(withoutProcessorReader)
		.writer(withoutProcessorWriter)
		.build();
	}
	
	public Step stepWithTasklet() {
		return stepBuilderFactory.get("Step with tasklet")
		.tasklet(taskletData())
		.build();
	}
	
	public Tasklet taskletData() {
		return new Tasklet() {
			
			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
				System.out.println("We can use Chunk with tasklet ");
				return RepeatStatus.FINISHED;
			}
		};
	}

}
