package com.pool.configuration.batch.writer;

import java.util.List;

import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Service;

import com.pool.modal.StudentJson;

@Service
public class StudentJsonItemWriter implements ItemWriter<StudentJson> {

	@Override
	public void write(List<? extends StudentJson> items) throws Exception {
		System.out.println("JSON FILE PROSESSING");
		items.forEach(System.out::println);
	}

}
