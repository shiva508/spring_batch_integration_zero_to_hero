package com.pool.configuration.batch.reader;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.stereotype.Component;

@Component
public class SecondItemWithoutProcessorReader implements ItemReader<Integer> {

	List<Integer> list = IntStream.range(0, 10).boxed().collect(Collectors.toList());
	int index = 0;

	@Override
	public Integer read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		System.out.println("Without Prosessor Reader");
		Integer item;
		if (index < list.size()) {
			item = list.get(index);
			index++;
			return item;
		}
		index=0;
		return null;
	}

}
