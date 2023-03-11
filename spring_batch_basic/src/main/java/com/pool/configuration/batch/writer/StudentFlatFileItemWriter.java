package com.pool.configuration.batch.writer;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Service;
import com.pool.modal.StudentCsv;

@Service
public class StudentFlatFileItemWriter implements ItemWriter<StudentCsv> {

	@Override
	public void write(Chunk<? extends StudentCsv> chunk) throws Exception {
		System.out.println("Process Started");
		chunk.getItems().forEach(System.out::println);
	}
}
