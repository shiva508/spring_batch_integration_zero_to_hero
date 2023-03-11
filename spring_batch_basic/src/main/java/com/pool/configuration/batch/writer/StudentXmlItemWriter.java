package com.pool.configuration.batch.writer;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Service;
import com.pool.modal.StudentXml;

@Service
public class StudentXmlItemWriter implements ItemWriter<StudentXml> {

	@Override
	public void write(Chunk<? extends StudentXml> chunk) throws Exception {
		System.out.println("Xml Processing");
		chunk.getItems().forEach(System.out::println);
	}
}
