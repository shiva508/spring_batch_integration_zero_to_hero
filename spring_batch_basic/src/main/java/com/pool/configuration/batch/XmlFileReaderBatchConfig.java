package com.pool.configuration.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import com.pool.configuration.batch.writer.StudentXmlItemWriter;
import com.pool.modal.StudentXml;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
public class XmlFileReaderBatchConfig {
	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;

	private final StudentXmlItemWriter studentXmlItemWriter;

	public XmlFileReaderBatchConfig(JobRepository jobRepository,
									PlatformTransactionManager transactionManager,
									StudentXmlItemWriter studentXmlItemWriter) {
		this.jobRepository = jobRepository;
		this.transactionManager = transactionManager;
		this.studentXmlItemWriter = studentXmlItemWriter;
	}

	@Bean
	public Job xmlFileJob() {
		return new JobBuilder("xmlFileJob",jobRepository)
								.incrementer(new RunIdIncrementer())
								.start(xmlFileStep())
								.build();																																														
	}

	@Bean
	public Step xmlFileStep() {

		return new StepBuilder("xmlFileStep",jobRepository)
								.<StudentXml,StudentXml>chunk(2,transactionManager)
								.reader(staxEventItemReader(null))
								//.writer(studentXmlItemWriter)
								.writer(staxEventItemWriter(null))
								.build();
	}
	
	@StepScope
	@Bean
	public StaxEventItemReader<StudentXml> staxEventItemReader(@Value("#{jobParameters['inputFile']}") FileSystemResource fileSystemResource){
		StaxEventItemReader<StudentXml> staxEventItemReader=new StaxEventItemReader<>();
		staxEventItemReader.setResource(fileSystemResource);
		staxEventItemReader.setFragmentRootElementName("student");
		Jaxb2Marshaller jaxb2Marshaller=new Jaxb2Marshaller();
		jaxb2Marshaller.setClassesToBeBound(StudentXml.class);
		staxEventItemReader.setUnmarshaller(jaxb2Marshaller);
		return staxEventItemReader;
	}
	
	@StepScope
	@Bean
	public StaxEventItemWriter<StudentXml> staxEventItemWriter(@Value("#{jobParameters['outputFile']}") FileSystemResource fileSystemResource){
		StaxEventItemWriter<StudentXml> staxEventItemWriter=new StaxEventItemWriter<>();
		staxEventItemWriter.setResource(fileSystemResource);
		staxEventItemWriter.setRootTagName("student");
		Jaxb2Marshaller jaxb2Marshaller=new Jaxb2Marshaller();
		jaxb2Marshaller.setClassesToBeBound(StudentXml.class);
		staxEventItemWriter.setMarshaller(jaxb2Marshaller);
		return staxEventItemWriter;
	}
}
