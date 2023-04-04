package com.pool.configuration.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.pool.configuration.batch.processor.SecondItemProcessor;
import com.pool.configuration.batch.reader.SecondItemReader;
import com.pool.configuration.batch.writer.SecondItemWriter;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
public class ChunkOrientedImageBatchConfig {

	private final JobRepository jobRepository;

	private final PlatformTransactionManager transactionManager;

	private final SecondItemReader secondItemReader;

	private final SecondItemWriter secondItemWriter;

	private final SecondItemProcessor secondItemProcessor;

	@Autowired
	public ChunkOrientedImageBatchConfig(JobRepository jobRepository,
										 PlatformTransactionManager transactionManager,
										 SecondItemReader secondItemReader,
										 SecondItemWriter secondItemWriter,
			SecondItemProcessor secondItemProcessor) {
		this.jobRepository = jobRepository;
		this.transactionManager = transactionManager;
		this.secondItemReader = secondItemReader;
		this.secondItemWriter = secondItemWriter;
		this.secondItemProcessor = secondItemProcessor;
	}

	@Bean
	public Job secondJob() {
		return new JobBuilder("secondJob",jobRepository)
		.incrementer(new RunIdIncrementer())
		.start(secondChunkStep())
		.build();
	}

	@Bean
	public Step secondChunkStep() {
		return new StepBuilder("secondChunkStep",jobRepository)
		.<Integer,Long>chunk(2,transactionManager)
		.reader(secondItemReader)
		.processor(secondItemProcessor)
		.writer(secondItemWriter)
		.build();
	}
}
