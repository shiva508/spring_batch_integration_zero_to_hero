package com.pool.configuration.batch.writer.header;

import java.io.IOException;
import java.io.Writer;

import org.springframework.batch.item.file.FlatFileHeaderCallback;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StudentFlatFileHeaderCallback implements FlatFileHeaderCallback {

	@Value("${studentjdbc.flatfile.header}")
	private String headerNames;
	@Override
	public void writeHeader(Writer writer) throws IOException {
		writer.write(headerNames);
	}

}
