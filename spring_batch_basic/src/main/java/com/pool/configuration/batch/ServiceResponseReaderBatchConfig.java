package com.pool.configuration.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.adapter.ItemReaderAdapter;
import org.springframework.batch.item.adapter.ItemWriterAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.pool.configuration.batch.writer.ServiceStudentItemWriter;
import com.pool.modal.StudentResponse;
import com.pool.service.StudentService;

@Configuration
@EnableBatchProcessing
public class ServiceResponseReaderBatchConfig {
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	@Autowired
	private ServiceStudentItemWriter serviceStudentItemWriter;
	
	@Autowired
	private StudentService studentService;
	
	
	@Bean
	public Job serviceStudentJob() {
		return jobBuilderFactory
				                .get("service Student Job")
				                .incrementer(new RunIdIncrementer())
				                .start(serviceStudentStep())
				                .build();
	}

	
	public Step serviceStudentStep() {
		return stepBuilderFactory
							    .get("JDBC Student Step")
								.<StudentResponse,StudentResponse>chunk(2)
								.reader(itemReaderAdapter())
								//.writer(serviceStudentItemWriter)
								.writer(itemWriterAdapter())
								.build();
	}
	
	public ItemReaderAdapter<StudentResponse> itemReaderAdapter(){
		ItemReaderAdapter<StudentResponse> itemReaderAdapter=new ItemReaderAdapter<>();
		itemReaderAdapter.setTargetObject(studentService);
		itemReaderAdapter.setTargetMethod("studentResponse");
		itemReaderAdapter.setArguments(new Object[] {"shiva1"});
		return itemReaderAdapter;
	}
	
	public ItemWriterAdapter<StudentResponse> itemWriterAdapter() {
		ItemWriterAdapter<StudentResponse> itemWriterAdapter=new ItemWriterAdapter<>();
		itemWriterAdapter.setTargetObject(studentService);
		itemWriterAdapter.setTargetMethod("saveStudentRequest");
		return itemWriterAdapter;
	}
	
}
