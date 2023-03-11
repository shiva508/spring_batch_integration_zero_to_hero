package com.pool.configuration.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonFileItemWriter;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import com.pool.configuration.batch.writer.StudentJsonItemWriter;
import com.pool.modal.StudentJson;

@Configuration
@EnableBatchProcessing
public class JsonFileReaderBatchConfig {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Autowired
	private StudentJsonItemWriter studentJsonItemWriter;

	@Bean
	public Job jsonFileJob() {
		return jobBuilderFactory
								.get("Json File Job")
								.incrementer(new RunIdIncrementer())
								.start(jsonFileStep())
								.build();
	}

	public Step jsonFileStep() {
		return stepBuilderFactory
								.get("Json File Step")
								.<StudentJson, StudentJson>chunk(2)
								.reader(jsonItemReader(null))
								//.writer(studentJsonItemWriter)
								.writer(jsonFileItemWriter(null))
								.build();
	}

	@StepScope
	@Bean
	public JsonItemReader<StudentJson> jsonItemReader(
			@Value("#{jobParameters['inputFile']}") FileSystemResource fileSystemResource) {
		JsonItemReader<StudentJson> jsonItemReader = new JsonItemReader<>();
		jsonItemReader.setResource(fileSystemResource);
		JacksonJsonObjectReader<StudentJson> jacksonJsonObjectReader = new JacksonJsonObjectReader<>(StudentJson.class);
		jsonItemReader.setJsonObjectReader(jacksonJsonObjectReader);
		jsonItemReader.setMaxItemCount(9);
		jsonItemReader.setCurrentItemCount(2);
		return jsonItemReader;
	}

	@StepScope
	@Bean
	public JsonFileItemWriter<StudentJson> jsonFileItemWriter(
			@Value("#{jobParameters['outputFile']}") FileSystemResource fileSystemResource) {
		JacksonJsonObjectMarshaller<StudentJson> jsonObjectMarshaller = new JacksonJsonObjectMarshaller<>();
		JsonFileItemWriter<StudentJson> jsonFileItemWriter = new JsonFileItemWriter<>(fileSystemResource,
				jsonObjectMarshaller);
		return jsonFileItemWriter;
	}

}
