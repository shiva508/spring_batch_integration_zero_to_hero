package com.pool.configuration.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import com.pool.configuration.batch.processor.CustomerProcessor;
import com.pool.configuration.batch.writer.customer.CustomerItemWriter;
import com.pool.domin.Customer;
import com.pool.modal.CustomerModel;
import com.pool.repository.CustomerRepository;

@Configuration
@EnableBatchProcessing
public class CustomerCsvFileBatchConfig {

	private final JobBuilderFactory jobBuilderFactory;
	
	private final StepBuilderFactory stepBuilderFactory;
	
	private final CustomerItemWriter customerItemWriter;
	
	private final CustomerRepository customerRepository;
	
	private final CustomerProcessor customerProcessor;
	
	private FlatFileItemReader<CustomerModel> csvCustomerfileItemReader;
	
	@StepScope
	@Bean
	public FlatFileItemReader<CustomerModel> getCsvCustomerfileItemReader() {
		return csvCustomerfileItemReader;
	}


	public void setCsvCustomerfileItemReader(FlatFileItemReader<CustomerModel> csvCustomerfileItemReader) {
		this.csvCustomerfileItemReader = csvCustomerfileItemReader;
	}


	public CustomerCsvFileBatchConfig(JobBuilderFactory jobBuilderFactory, 
									  StepBuilderFactory stepBuilderFactory,
									  CustomerItemWriter customerItemWriter,
									  CustomerRepository customerRepository,
									  CustomerProcessor customerProcessor) {
		this.jobBuilderFactory = jobBuilderFactory;
		this.stepBuilderFactory = stepBuilderFactory;
		this.customerItemWriter = customerItemWriter;
		this.customerRepository = customerRepository;
		this.customerProcessor = customerProcessor;
	}
	
	
	@Bean
	public Job customerJob() {
		return jobBuilderFactory.get("customerJob")
				.incrementer(new RunIdIncrementer())
				.start(customerCsvStep())
				.build();
	}
	
	public Step customerCsvStep() {
		return stepBuilderFactory.get("customerCsvStep")
				.<CustomerModel,Customer>chunk(5)
				.reader(getCsvCustomerfileItemReader())
				.processor(customerProcessor)
				.writer(customerItemWriter)
				.faultTolerant()
				.skipLimit(100)
				.skip(FlatFileParseException.class)
				//.noSkip(IllegalArgumentException.class)
				.build();		
	}
	
	@Bean
	public RepositoryItemWriter<Customer> repositoryItemWriter(){
		RepositoryItemWriter<Customer> repositoryItemWriter=new RepositoryItemWriter<>();
		repositoryItemWriter.setRepository(customerRepository);
		repositoryItemWriter.setMethodName("saveAll");
		return repositoryItemWriter;
	}
	
	
	
	
}
