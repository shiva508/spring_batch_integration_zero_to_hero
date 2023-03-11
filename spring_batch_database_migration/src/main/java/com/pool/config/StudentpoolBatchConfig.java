package com.pool.config;

import javax.sql.DataSource;

import com.pool.entity.StudentOne;
import com.pool.entity.StudentTwo;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
public class StudentpoolBatchConfig {

	@Autowired
	private JobRepository jobRepository;
	@Autowired
	private PlatformTransactionManager transactionManager;
	@Autowired
	@Qualifier("datasource")
	private DataSource datasource;

	@Autowired
	@Qualifier("datasourcetwo")
	private DataSource datasourcetwo;

	@Autowired
	@Qualifier("datasourceOneEntityManagerFactory")
	private EntityManagerFactory datasourceOneEntityManagerFactory;

	@Autowired
	@Qualifier("datasourceTwoEntityManagerFactory")
	private EntityManagerFactory datasourceTwoEntityManagerFactory;
	
	@Autowired
	private StudentItemProcessor studentItemProcessor;
	
	@Autowired
	private JpaTransactionManager jpaTransactionManager;

	@Bean
	public Job jpaBatchJob( ) {
		return new JobBuilder("Jpa Batch Job",jobRepository)
				.incrementer(new RunIdIncrementer())
				.start(jpaBatchStep())
				.build();
	}

	
	public Step jpaBatchStep() {
		return new StepBuilder("Jpa Batch Step",jobRepository)
								.<StudentTwo, StudentOne>chunk(2,transactionManager)
								.reader(jpaCursorItemReader())
								.processor(studentItemProcessor)
								.writer(jpaItemWriter())
								.transactionManager(jpaTransactionManager)
								.build();
	}

	public JpaCursorItemReader<StudentTwo> jpaCursorItemReader() {
		JpaCursorItemReader<StudentTwo> jpaCursorItemReader = new JpaCursorItemReader<>();
		jpaCursorItemReader.setEntityManagerFactory(datasourceTwoEntityManagerFactory);
		jpaCursorItemReader.setQueryString("FROM StudentTwo");
		return jpaCursorItemReader;
	}

	public JpaItemWriter<StudentOne> jpaItemWriter() {
		JpaItemWriter<StudentOne> jpaItemWriter=new JpaItemWriter<>();
		jpaItemWriter.setEntityManagerFactory(datasourceOneEntityManagerFactory);
		return jpaItemWriter;
	}
}
