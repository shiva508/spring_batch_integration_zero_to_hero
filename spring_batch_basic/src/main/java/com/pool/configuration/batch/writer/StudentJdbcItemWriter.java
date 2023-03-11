package com.pool.configuration.batch.writer;

import java.util.List;

import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Service;

import com.pool.modal.StudentJdbc;
import com.pool.modal.StudentJson;

@Service
public class StudentJdbcItemWriter implements ItemWriter<StudentJdbc> {

	@Override
	public void write(List<? extends StudentJdbc> items) throws Exception {
		System.out.println("JDBC PROSESSING");
		items.forEach(System.out::println);
	}

}
