package com.pool.configuration.batch.reader;

import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.pool.domin.Customer;
import com.pool.modal.CustomerModel;
import com.pool.modal.StudentCsv;

@Service
public class StudentFlatFileItemReader {
	public FlatFileItemReader<StudentCsv> flatFileItemReader(String csvpath){
		FlatFileItemReader<StudentCsv> itemReader=new FlatFileItemReader<>();
		Resource resource = new FileSystemResource(csvpath);
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
		itemReader.setLinesToSkip(1);
		return itemReader;
	}
	
	
	public FlatFileItemReader<CustomerModel> csvCustomerfileItemReader(MultipartFile multipartFile){
		FlatFileItemReader<CustomerModel> itemReader=new FlatFileItemReader<>();
		Resource fileSystemResource=multipartFile.getResource();
		itemReader.setResource(fileSystemResource);
		itemReader.setLineMapper(customerlineMapper());
		itemReader.setLinesToSkip(1);
		itemReader.setName("customer");
		
		return itemReader;
	}
	
	public LineMapper<CustomerModel> customerlineMapper(){
		DefaultLineMapper<CustomerModel> defaultLineMapper=new DefaultLineMapper<>();
		DelimitedLineTokenizer delimitedLineTokenizer=new DelimitedLineTokenizer();
		delimitedLineTokenizer.setDelimiter(",");
		delimitedLineTokenizer.setStrict(false);
		delimitedLineTokenizer.setNames("id", "firstName", "lastName", "email", "gender", "contactNo", "country", "dob", "age");
		
		BeanWrapperFieldSetMapper<CustomerModel> beanWrapperFieldSetMapper=new BeanWrapperFieldSetMapper<>();
		beanWrapperFieldSetMapper.setTargetType(CustomerModel.class);
		
		defaultLineMapper.setLineTokenizer(delimitedLineTokenizer);
		defaultLineMapper.setFieldSetMapper(beanWrapperFieldSetMapper);
		
		return defaultLineMapper;
	}
	
}
