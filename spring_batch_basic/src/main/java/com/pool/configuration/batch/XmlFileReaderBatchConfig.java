package com.pool.configuration.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import com.pool.configuration.batch.writer.StudentXmlItemWriter;
import com.pool.modal.StudentXml;

@Configuration
@EnableBatchProcessing
public class XmlFileReaderBatchConfig {
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	@Autowired
	private StudentXmlItemWriter studentXmlItemWriter;
	
	@Bean
	public Job xmlFileJob() {
		return jobBuilderFactory
								.get("Xml File Job")
								.incrementer(new RunIdIncrementer())
								.start(xmlFileStep())
								.build();																																														
	}
	
	public Step xmlFileStep() {
		return stepBuilderFactory
								.get("XML File Step")
								.<StudentXml,StudentXml>chunk(2)
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
