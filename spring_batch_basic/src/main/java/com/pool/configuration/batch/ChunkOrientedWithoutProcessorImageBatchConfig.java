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
import com.pool.configuration.batch.reader.SecondItemWithoutProcessorReader;
import com.pool.configuration.batch.writer.SecondItemWithoutProcessorWriter;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
public class ChunkOrientedWithoutProcessorImageBatchConfig {

	private final JobRepository jobRepository;

	private final PlatformTransactionManager transactionManager;

	private final SecondItemWithoutProcessorReader withoutProcessorReader;

	private final SecondItemWithoutProcessorWriter withoutProcessorWriter;

	public ChunkOrientedWithoutProcessorImageBatchConfig(JobRepository jobRepository,
														 PlatformTransactionManager transactionManager,
														 SecondItemWithoutProcessorReader withoutProcessorReader,
															SecondItemWithoutProcessorWriter withoutProcessorWriter) {
		this.jobRepository = jobRepository;
		this.transactionManager=transactionManager;
		this.withoutProcessorReader = withoutProcessorReader;
		this.withoutProcessorWriter = withoutProcessorWriter;
	}
	
	@Bean
	public Job jobWithoutProcessor() {
		return new JobBuilder("jobWithoutProcessor",jobRepository)
		.incrementer(new RunIdIncrementer())
		.start(stepWithoutProcessor())
		.next(stepWithTasklet())
		.build();
	}

	@Bean
	public Step stepWithoutProcessor() {
		return new StepBuilder("stepWithoutProcessor",jobRepository)
		.<Integer,Integer>chunk(3,transactionManager)
		.reader(withoutProcessorReader)
		.writer(withoutProcessorWriter)
		.build();
	}

	@Bean
	public Step stepWithTasklet() {
		return new StepBuilder("stepWithoutProcessor",jobRepository)
		.tasklet(taskletData(),transactionManager)
		.build();
	}

	@Bean
	public Tasklet taskletData() {
		return (contribution, chunkContext) -> {
			System.out.println("We can use Chunk with tasklet ");
			return RepeatStatus.FINISHED;
		};
	}

}
