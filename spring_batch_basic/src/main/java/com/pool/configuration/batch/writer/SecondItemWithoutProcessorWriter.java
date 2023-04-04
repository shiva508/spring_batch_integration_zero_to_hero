package com.pool.configuration.batch.writer;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
public class SecondItemWithoutProcessorWriter implements ItemWriter<Integer> {

	@Override
	public void write(Chunk<? extends Integer> chunk) throws Exception {
		System.out.println("Without Prosessor Writer: " + chunk.size());
		chunk.getItems().stream().forEach(System.out::println);
	}
}
