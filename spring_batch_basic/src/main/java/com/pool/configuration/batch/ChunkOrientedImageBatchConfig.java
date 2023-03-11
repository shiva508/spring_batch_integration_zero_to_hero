package com.pool.configuration.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.pool.configuration.batch.processor.SecondItemProcessor;
import com.pool.configuration.batch.reader.SecondItemReader;
import com.pool.configuration.batch.writer.SecondItemWriter;

@Configuration
@EnableBatchProcessing
public class ChunkOrientedImageBatchConfig {

	private JobBuilderFactory jobBuilderFactory;

	private StepBuilderFactory stepBuilderFactory;

	private SecondItemReader secondItemReader;

	private SecondItemWriter secondItemWriter;

	private SecondItemProcessor secondItemProcessor;

	@Autowired
	public ChunkOrientedImageBatchConfig(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory,
			SecondItemReader secondItemReader, SecondItemWriter secondItemWriter,
			SecondItemProcessor secondItemProcessor) {
		this.jobBuilderFactory = jobBuilderFactory;
		this.stepBuilderFactory = stepBuilderFactory;
		this.secondItemReader = secondItemReader;
		this.secondItemWriter = secondItemWriter;
		this.secondItemProcessor = secondItemProcessor;
	}

	@Bean
	public Job secondJob() {
		return jobBuilderFactory.get("Second Job")
		.incrementer(new RunIdIncrementer())
		.start(secondChunkStep())
		.build();
	}

	public Step secondChunkStep() {
		return  stepBuilderFactory.get("Second Step")
		.<Integer,Long>chunk(2)
		.reader(secondItemReader)
		.processor(secondItemProcessor)
		.writer(secondItemWriter)
		.build();
	}
}
