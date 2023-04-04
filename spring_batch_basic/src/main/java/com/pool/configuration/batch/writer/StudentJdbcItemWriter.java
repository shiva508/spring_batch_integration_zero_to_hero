package com.pool.configuration.batch.writer;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Service;
import com.pool.modal.StudentJdbc;

@Service
public class StudentJdbcItemWriter implements ItemWriter<StudentJdbc> {

	@Override
	public void write(Chunk<? extends StudentJdbc> chunk) throws Exception {
		System.out.println("JDBC PROSESSING");
		chunk.getItems().forEach(System.out::println);
	}
}
