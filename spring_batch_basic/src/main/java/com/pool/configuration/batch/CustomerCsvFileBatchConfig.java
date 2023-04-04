package com.pool.configuration.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.pool.configuration.batch.processor.CustomerProcessor;
import com.pool.configuration.batch.writer.customer.CustomerItemWriter;
import com.pool.domin.Customer;
import com.pool.modal.CustomerModel;
import com.pool.repository.CustomerRepository;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
public class CustomerCsvFileBatchConfig {

	private final JobRepository jobRepository;

	private final PlatformTransactionManager transactionManager;
	
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


	public CustomerCsvFileBatchConfig(JobRepository jobRepository,
									  PlatformTransactionManager transactionManager,
									  CustomerItemWriter customerItemWriter,
									  CustomerRepository customerRepository,
									  CustomerProcessor customerProcessor) {
		this.jobRepository = jobRepository;
		this.transactionManager = transactionManager;
		this.customerItemWriter = customerItemWriter;
		this.customerRepository = customerRepository;
		this.customerProcessor = customerProcessor;
	}
	
	
	@Bean
	public Job customerJob() {
		return new JobBuilder("customerJob",jobRepository)
				.incrementer(new RunIdIncrementer())
				.start(customerCsvStep())
				.build();
	}
	
	public Step customerCsvStep() {

		return new StepBuilder("customerCsvStep",jobRepository)
				.<CustomerModel,Customer>chunk(5,transactionManager)
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
