package com.pool.configuration.batch;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.JsonFileItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import com.pool.configuration.batch.processor.JdbcReaderJsonWriterItemProcessor;
import com.pool.configuration.batch.writer.StudentJdbcItemWriter;
import com.pool.configuration.batch.writer.header.StudentFlatFileHeaderCallback;
import com.pool.modal.StudentJdbc;
import com.pool.modal.StudentJdbcJson;
import com.pool.modal.StudentJson;

@Configuration
@EnableBatchProcessing
public class JdbcReaderJsonWriterBatchConfig {
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	@Autowired
	//@Qualifier("dataSourceShiva")
	private DataSource dataSource;
	
	@Autowired
	private StudentFlatFileHeaderCallback fileHeaderCallback;
	
	@Autowired
	private JdbcReaderJsonWriterItemProcessor jdbcReaderJsonWriterItemProcessor;
	
	@Bean
	public Job jdbcJsonStudentJob() {
		return jobBuilderFactory
				                .get("Jdbc Student Job")
				                .incrementer(new RunIdIncrementer())
				                .start(jdbcJsonStudentStep())
				                .build();
	}

	
	public Step jdbcJsonStudentStep() {
		return stepBuilderFactory
							    .get("JDBC Student Step")
								.<StudentJdbc,StudentJdbcJson>chunk(2)
								.reader(jdbcCursorItemReader())
								//.writer(studentJdbcItemWriter)
								.processor(jdbcReaderJsonWriterItemProcessor)
								.writer(jdbcJsonFileItemWriter(null))
								.build();
	}
	
	
	public JdbcCursorItemReader<StudentJdbc> jdbcCursorItemReader(){
		JdbcCursorItemReader<StudentJdbc> jdbcCursorItemReader=new JdbcCursorItemReader<>();
		jdbcCursorItemReader.setDataSource(dataSource);
		jdbcCursorItemReader.setSql("select id, first_name as firstName,last_name as lastName,email from student");
		BeanPropertyRowMapper<StudentJdbc> beanPropertyRowMapper=new BeanPropertyRowMapper<>();
		beanPropertyRowMapper.setMappedClass(StudentJdbc.class);
		jdbcCursorItemReader.setRowMapper(beanPropertyRowMapper);
		return jdbcCursorItemReader;
	}
	
	@StepScope
	@Bean
	public JsonFileItemWriter<StudentJdbcJson> jdbcJsonFileItemWriter(@Value("${jobParameters[outputFile]}")FileSystemResource fileSystemResource){
		JacksonJsonObjectMarshaller<StudentJdbcJson> marshaller=new JacksonJsonObjectMarshaller<>();
		JsonFileItemWriter<StudentJdbcJson> jdbcJsonFileItemWriter=new JsonFileItemWriter<>(fileSystemResource, marshaller);
		return jdbcJsonFileItemWriter;
	}
	
}
