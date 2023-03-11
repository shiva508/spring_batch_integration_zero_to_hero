package com.pool.configuration.batch.writer;

import java.util.List;

import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
public class SecondItemWithoutProcessorWriter implements ItemWriter<Integer> {

	@Override
	public void write(List<? extends Integer> items) throws Exception {
		System.out.println("Without Prosessor Writer: " + items.size());
		items.stream().forEach(System.out::println);
	}

}
