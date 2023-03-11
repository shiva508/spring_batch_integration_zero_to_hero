package com.pool.configuration.batch.writer;

import java.util.List;

import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Service;

import com.pool.modal.StudentXml;

@Service
public class StudentXmlItemWriter implements ItemWriter<StudentXml> {

	@Override
	public void write(List<? extends StudentXml> items) throws Exception {
		System.out.println("Xml Processing");
		items.forEach(System.out::println);
	}

}
