package com.pool.configuration.batch.writer;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Service;
import com.pool.modal.StudentJson;

@Service
public class StudentJsonItemWriter implements ItemWriter<StudentJson> {

	@Override
	public void write(Chunk<? extends StudentJson> chunk) throws Exception {
		System.out.println("JSON FILE PROSESSING");
		chunk.getItems().forEach(System.out::println);
	}
}
