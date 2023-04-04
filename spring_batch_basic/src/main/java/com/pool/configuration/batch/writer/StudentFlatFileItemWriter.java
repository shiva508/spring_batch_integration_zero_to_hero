package com.pool.configuration.batch.writer;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Service;
import com.pool.modal.StudentCsv;

@Service
public class StudentFlatFileItemWriter implements ItemWriter<StudentCsv> {
	private StepExecution stepExecution;

	@Override
	public void write(Chunk<? extends StudentCsv> chunk) throws Exception {
		System.out.println("Process Started");
		stepExecution.getExecutionContext().put("studentId",chunk.getItems().size());
		chunk.getItems().forEach(System.out::println);
	}

}
