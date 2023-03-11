package com.pool.configuration.batch.writer;

import java.util.List;

import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Service;

import com.pool.modal.StudentCsv;

@Service
public class StudentFlatFileItemWriter implements ItemWriter<StudentCsv> {

	@Override
	public void write(List<? extends StudentCsv> items) throws Exception {
		System.out.println("Process Started");
		items.forEach(data->{
			System.out.println(data);
		});
	}

}
