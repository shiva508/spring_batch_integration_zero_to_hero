package com.pool.configuration.batch.writer;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Service;
import com.pool.modal.StudentResponse;

@Service
public class ServiceStudentItemWriter implements ItemWriter<StudentResponse> {

	@Override
	public void write(Chunk<? extends StudentResponse> chunk) throws Exception {
		System.out.println("Response PROSESSING");
		chunk.getItems().forEach(System.out::println);
	}
}
