package com.pool.configuration.batch;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.JsonFileItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import com.pool.configuration.batch.processor.JdbcReaderJsonWriterItemProcessor;
import com.pool.configuration.batch.writer.header.StudentFlatFileHeaderCallback;
import com.pool.modal.StudentJdbc;
import com.pool.modal.StudentJdbcJson;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
public class JdbcReaderJsonWriterBatchConfig {
	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;
	
	//@Autowired
	//@Qualifier("dataSourceShiva")
	private final DataSource dataSource;
	
	//@Autowired
	private final StudentFlatFileHeaderCallback fileHeaderCallback;
	
	//@Autowired
	private final JdbcReaderJsonWriterItemProcessor jdbcReaderJsonWriterItemProcessor;

	public JdbcReaderJsonWriterBatchConfig(JobRepository jobRepository,
										   PlatformTransactionManager transactionManager,
										   DataSource dataSource,
										   StudentFlatFileHeaderCallback fileHeaderCallback,
										   JdbcReaderJsonWriterItemProcessor jdbcReaderJsonWriterItemProcessor) {
		this.jobRepository = jobRepository;
		this.transactionManager = transactionManager;
		this.dataSource = dataSource;
		this.fileHeaderCallback = fileHeaderCallback;
		this.jdbcReaderJsonWriterItemProcessor = jdbcReaderJsonWriterItemProcessor;
	}

	@Bean
	public Job jdbcJsonStudentJob() {

		return new JobBuilder("jdbcJsonStudentJob",jobRepository)
				                .incrementer(new RunIdIncrementer())
				                .start(jdbcJsonStudentStep())
				                .build();
	}

	@Bean
	public Step jdbcJsonStudentStep() {

		return new StepBuilder("jdbcJsonStudentStep",jobRepository)
								.<StudentJdbc,StudentJdbcJson>chunk(2,transactionManager)
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
		return new JsonFileItemWriter<>(fileSystemResource, marshaller);
	}
	
}
