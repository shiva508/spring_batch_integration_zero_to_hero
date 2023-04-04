package com.pool.configuration.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.listener.ExecutionContextPromotionListener;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.skip.AlwaysSkipItemSkipPolicy;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import com.pool.configuration.batch.listener.SkipBadRecordListener;
import com.pool.configuration.batch.reader.StudentFlatFileItemReader;
import com.pool.configuration.batch.writer.StudentFlatFileItemWriter;
import com.pool.modal.StudentCsv;

@Configuration
@EnableBatchProcessing
public class FlatFileReaderBatchConfig {
	private final JobRepository jobRepository;

	private final PlatformTransactionManager transactionManager;
	

	private final StudentFlatFileItemReader studentFlatFileItemReader;
	

	private final StudentFlatFileItemWriter studentFlatFileItemWriter;

	private final SkipBadRecordListener skipBadRecordListener;
	
	@Value("${csv_file_path}")
	private String csvFilePath;

	public FlatFileReaderBatchConfig(JobRepository jobRepository,
									 PlatformTransactionManager transactionManager,
									 StudentFlatFileItemReader studentFlatFileItemReader,
									 StudentFlatFileItemWriter studentFlatFileItemWriter,
									 SkipBadRecordListener skipBadRecordListener) {
		this.jobRepository = jobRepository;
		this.transactionManager = transactionManager;
		this.studentFlatFileItemReader = studentFlatFileItemReader;
		this.studentFlatFileItemWriter = studentFlatFileItemWriter;
		this.skipBadRecordListener = skipBadRecordListener;
	}

	@Bean(name="flatFileJob")
	public Job flatFileJob() {

		return new JobBuilder("flatFileJob",jobRepository)
								.incrementer(new RunIdIncrementer())
								.start(flatFileStep())
								//.next(saveRoleStep())
								.build();
	}

	/*@Bean
	public Step saveRoleStep(){
		return new StepBuilder("saveRoleStep",jobRepository)
				.chunk(10,transactionManager)
				.writer(chunk -> {
					//System.out.println(stepExecution.getExecutionContext().get("studentId"));
				}).build();
	}*/

	@Bean
	public Step flatFileStep() {
		DefaultTransactionAttribute attribute = new DefaultTransactionAttribute();
		attribute.setPropagationBehavior(Propagation.REQUIRED.value());
		attribute.setIsolationLevel(Isolation.DEFAULT.value());
		attribute.setTimeout(30);
		return new StepBuilder("flatFileStep",jobRepository)
				                .<StudentCsv,StudentCsv>chunk(2,transactionManager)
				                //.reader(flatFileItemReader())
				                //.reader(studentFlatFileItemReader.flatFileItemReader(csvFilePath))
				                //.reader(flatFileItemReaderFromJobParam(null))
								.listener(contextPromotionListener())
				                .reader(customFlatFileItemReaderFromJobParam(null,null))
				                .writer(studentFlatFileItemWriter)
				                .faultTolerant()
				                .skip(Throwable.class)
				                //.skip(FlatFileParseException.class)
				                //.skipLimit(Integer.MAX_VALUE)
				                .skipPolicy(new AlwaysSkipItemSkipPolicy())
				                //.retryLimit(1)
				                //.retry(Throwable.class)

				                .listener(skipBadRecordListener)
				                //.transactionManager(jpaTransactionManager)
				                .transactionAttribute(attribute)
				                .build();
	}
	
	public FlatFileItemReader<StudentCsv> flatFileItemReader(){
		FlatFileItemReader<StudentCsv> itemReader=new FlatFileItemReader<>();
		Resource resource = new FileSystemResource(csvFilePath);
		itemReader.setResource(resource);
		
		DefaultLineMapper< StudentCsv> lineMapper=new DefaultLineMapper<>();
		DelimitedLineTokenizer delimitedLineTokenizer=new DelimitedLineTokenizer();
		delimitedLineTokenizer.setNames("ID","First Name","Last Name",	"Email");
		lineMapper.setLineTokenizer(delimitedLineTokenizer);
		lineMapper.setFieldSetMapper(new BeanWrapperFieldSetMapper<StudentCsv>() {
			{
				setTargetType(StudentCsv.class);
			}
		});
		itemReader.setLineMapper(lineMapper);
		//itemReader.setLinesToSkip(1);
		return itemReader;
	}
	
	@Bean
	@StepScope
	public FlatFileItemReader<StudentCsv> flatFileItemReaderFromJobParam(@Value("#{jobParameters['inputFile']}") FileSystemResource fileSystemResource){
		FlatFileItemReader<StudentCsv> itemReader=new FlatFileItemReader<>();
		
		itemReader.setResource(fileSystemResource);
		
		DefaultLineMapper< StudentCsv> lineMapper=new DefaultLineMapper<>();
		DelimitedLineTokenizer delimitedLineTokenizer=new DelimitedLineTokenizer();
		delimitedLineTokenizer.setNames("ID","First Name","Last Name",	"Email");
		lineMapper.setLineTokenizer(delimitedLineTokenizer);
		lineMapper.setFieldSetMapper(new BeanWrapperFieldSetMapper<StudentCsv>() {
			{
				setTargetType(StudentCsv.class);
			}
		});
		itemReader.setLineMapper(lineMapper);
		//itemReader.setLinesToSkip(1);
		return itemReader;
	}
	
	@Bean
	@StepScope
	public FlatFileItemReader<StudentCsv> customFlatFileItemReaderFromJobParam(@Value("#{jobParameters['inputFile']}") FileSystemResource fileSystemResource, StepExecution stepExecution){
		FlatFileItemReader<StudentCsv> itemReader=new FlatFileItemReader<>();
		
		itemReader.setResource(fileSystemResource);
		
		DefaultLineMapper< StudentCsv> lineMapper=new DefaultLineMapper<>();
		DelimitedLineTokenizer delimitedLineTokenizer=new DelimitedLineTokenizer();
		delimitedLineTokenizer.setNames("ID","First Name","Last Name","Email");
		lineMapper.setLineTokenizer(delimitedLineTokenizer);
		BeanWrapperFieldSetMapper<StudentCsv> beanWrapperFieldSetMapper=new BeanWrapperFieldSetMapper<>();
		beanWrapperFieldSetMapper.setTargetType(StudentCsv.class);
		lineMapper.setFieldSetMapper(beanWrapperFieldSetMapper);
		itemReader.setLineMapper(lineMapper);
		itemReader.setLinesToSkip(1);
		return itemReader;
	}

	@Bean
	public ExecutionContextPromotionListener contextPromotionListener(){
		ExecutionContextPromotionListener  promotionListener=new ExecutionContextPromotionListener();
		promotionListener.setKeys(new String[]{"studentId","roleId"});
		return promotionListener;
	}

}
