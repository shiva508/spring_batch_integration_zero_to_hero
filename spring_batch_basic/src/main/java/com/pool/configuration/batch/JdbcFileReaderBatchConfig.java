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
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.ItemPreparedStatementSetter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import com.pool.configuration.batch.writer.StudentJdbcItemWriter;
import com.pool.configuration.batch.writer.header.StudentFlatFileHeaderCallback;
import com.pool.modal.StudentJdbc;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
public class JdbcFileReaderBatchConfig {
	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;
	private final StudentJdbcItemWriter studentJdbcItemWriter;
	
	/*@Autowired
	@Qualifier("dataSourceShiva")
	private DataSource dataSourceShiva;*/
	
	//@Autowired
	//@Qualifier("dataSourcebd")
	private final DataSource dataSource;
	private final StudentFlatFileHeaderCallback fileHeaderCallback;

	public JdbcFileReaderBatchConfig(JobRepository jobRepository,
									 PlatformTransactionManager transactionManager,
									 StudentJdbcItemWriter studentJdbcItemWriter,
									 DataSource dataSource,
									 StudentFlatFileHeaderCallback fileHeaderCallback) {
		this.jobRepository = jobRepository;
		this.transactionManager = transactionManager;
		this.studentJdbcItemWriter = studentJdbcItemWriter;
		this.dataSource = dataSource;
		this.fileHeaderCallback = fileHeaderCallback;
	}

	@Bean
	public Job jdbcStudentJob() {

		return new JobBuilder("jdbcStudentJob", jobRepository)
				                .incrementer(new RunIdIncrementer())
				                .start(jdbcStudentStep())
				                .build();
	}

	@Bean
	public Step jdbcStudentStep() {

		return new StepBuilder("jdbcStudentStep",jobRepository)
								.<StudentJdbc,StudentJdbc>chunk(2,transactionManager)
								.reader(jdbcCursorItemReader())
								//.writer(studentJdbcItemWriter)
								//.writer(flatFileItemWriter(null))
								.writer(jdbcBatchItemWriter())
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
	public FlatFileItemWriter<StudentJdbc> flatFileItemWriter(@Value("#{jobParameters['outputFile']}") FileSystemResource fileSystemResource){
		FlatFileItemWriter<StudentJdbc> flatFileItemWriter=new FlatFileItemWriter<>();
		flatFileItemWriter.setResource(fileSystemResource);
		BeanWrapperFieldExtractor<StudentJdbc> beanWrapperFieldExtractor=new BeanWrapperFieldExtractor<>();
		beanWrapperFieldExtractor.setNames(new String[] {"id","firstName","lastName","email"});
		beanWrapperFieldExtractor.afterPropertiesSet();
		DelimitedLineAggregator<StudentJdbc> delimitedLineAggregator=new DelimitedLineAggregator<>();
		delimitedLineAggregator.setDelimiter(",");
		delimitedLineAggregator.setFieldExtractor(beanWrapperFieldExtractor);
		flatFileItemWriter.setLineAggregator(delimitedLineAggregator);
		flatFileItemWriter.setHeaderCallback(fileHeaderCallback);
		return flatFileItemWriter;
	}
	
	@Bean
	public JdbcBatchItemWriter<StudentJdbc> jdbcBatchItemWriter(){
		JdbcBatchItemWriter<StudentJdbc> jdbcBatchItemWriter=new JdbcBatchItemWriter<>();
		jdbcBatchItemWriter.setDataSource(dataSource);
		jdbcBatchItemWriter.setSql("insert into student(id,first_name,last_name,email) values(:id,:firstName,:lastName,:email) ");
		BeanPropertyItemSqlParameterSourceProvider<StudentJdbc> sourceProvider=new BeanPropertyItemSqlParameterSourceProvider<>();
		jdbcBatchItemWriter.setItemSqlParameterSourceProvider(sourceProvider);
		return jdbcBatchItemWriter;
	}
	
	@Bean
	public JdbcBatchItemWriter<StudentJdbc> jdbcBatchItemWriterPrepared(){
		JdbcBatchItemWriter<StudentJdbc> jdbcBatchItemWriter=new JdbcBatchItemWriter<>();
		jdbcBatchItemWriter.setDataSource(dataSource);
		jdbcBatchItemWriter.setSql("insert into student(id,first_name,last_name,email) values(?,?,?,?) ");
		
		ItemPreparedStatementSetter<StudentJdbc> itemPreparedStatementSetter= (item, ps) -> {
			ps.setLong(1, item.getId());
			ps.setString(2, item.getFirstName());
			ps.setString(2, item.getLastName());
			ps.setString(2, item.getEmail());
		};
		BeanPropertyItemSqlParameterSourceProvider<StudentJdbc> sourceProvider=new BeanPropertyItemSqlParameterSourceProvider<>();
		jdbcBatchItemWriter.setItemPreparedStatementSetter(itemPreparedStatementSetter);
		return jdbcBatchItemWriter;
	}
	
}
