package com.pool.configuration.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.adapter.ItemReaderAdapter;
import org.springframework.batch.item.adapter.ItemWriterAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.pool.configuration.batch.writer.ServiceStudentItemWriter;
import com.pool.modal.StudentResponse;
import com.pool.service.StudentService;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
public class ServiceResponseReaderBatchConfig {
	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;

	private final ServiceStudentItemWriter serviceStudentItemWriter;
	private final StudentService studentService;

	public ServiceResponseReaderBatchConfig(JobRepository jobRepository,
											PlatformTransactionManager transactionManager,
											ServiceStudentItemWriter serviceStudentItemWriter,
											StudentService studentService) {
		this.jobRepository = jobRepository;
		this.transactionManager = transactionManager;
		this.serviceStudentItemWriter = serviceStudentItemWriter;
		this.studentService = studentService;
	}

	@Bean
	public Job serviceStudentJob() {
		return new JobBuilder("serviceStudentJob",jobRepository)
				                .incrementer(new RunIdIncrementer())
				                .start(serviceStudentStep())
				                .build();
	}


	@Bean
	public Step serviceStudentStep() {

		return new StepBuilder("serviceStudentStep",jobRepository)
								.<StudentResponse,StudentResponse>chunk(2,transactionManager)
								.reader(itemReaderAdapter())
								//.writer(serviceStudentItemWriter)
								.writer(itemWriterAdapter())
								.build();
	}
	@Bean
	public ItemReaderAdapter<StudentResponse> itemReaderAdapter(){
		ItemReaderAdapter<StudentResponse> itemReaderAdapter=new ItemReaderAdapter<>();
		itemReaderAdapter.setTargetObject(studentService);
		itemReaderAdapter.setTargetMethod("studentResponse");
		itemReaderAdapter.setArguments(new Object[] {"shiva1"});
		return itemReaderAdapter;
	}

	@Bean
	public ItemWriterAdapter<StudentResponse> itemWriterAdapter() {
		ItemWriterAdapter<StudentResponse> itemWriterAdapter=new ItemWriterAdapter<>();
		itemWriterAdapter.setTargetObject(studentService);
		itemWriterAdapter.setTargetMethod("saveStudentRequest");
		return itemWriterAdapter;
	}
	
}
