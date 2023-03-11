package com.pool.configuration.batch.writer;

import java.util.List;

import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Service;

import com.pool.modal.StudentJdbc;
import com.pool.modal.StudentJson;
import com.pool.modal.StudentResponse;

@Service
public class ServiceStudentItemWriter implements ItemWriter<StudentResponse> {

	@Override
	public void write(List<? extends StudentResponse> items) throws Exception {
		System.out.println("Response PROSESSING");
		items.forEach(System.out::println);
	}

}
