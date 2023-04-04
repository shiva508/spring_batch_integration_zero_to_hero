package com.pool.configuration.batch.listener;


import org.springframework.batch.core.SkipListener;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.beans.factory.annotation.Autowired;

import com.pool.domin.ErrorEntity;
import com.pool.modal.StudentCsv;
import com.pool.service.error.ErrorService;
import com.pool.util.BatchImageConstant;
import org.springframework.stereotype.Component;

@Component
public class SkipBadRecordListeber implements SkipListener<StudentCsv, StudentCsv> {

	@Autowired
	private ErrorService errorService;
	@Override
	public void onSkipInRead(Throwable throwable) {
		
		if (throwable instanceof FlatFileParseException) {
			String input = ((FlatFileParseException) throwable).getInput();
			Integer lineNumber = ((FlatFileParseException) throwable).getLineNumber();
			String message = ((FlatFileParseException) throwable).getMessage();
			ErrorEntity errorEntity=new ErrorEntity();
			errorEntity.setInput(input);
			errorEntity.setLineNumber(lineNumber);
			errorEntity.setMessage(message);
			errorEntity.setActionType(BatchImageConstant.READ);
			ErrorEntity saved =errorService.saveErrorRecord(errorEntity);
			System.out.println(saved);
		}
	}

	@Override
	public void onSkipInWrite(StudentCsv item, Throwable throwable) {
		if (throwable instanceof FlatFileParseException) {
			String input = ((FlatFileParseException) throwable).getInput();
			Integer lineNumber = ((FlatFileParseException) throwable).getLineNumber();
			String message = ((FlatFileParseException) throwable).getMessage();
			ErrorEntity errorEntity=new ErrorEntity();
			errorEntity.setInput(input);
			errorEntity.setLineNumber(lineNumber);
			errorEntity.setMessage(message);
			errorEntity.setActionType(BatchImageConstant.READ);
			ErrorEntity saved =errorService.saveErrorRecord(errorEntity);
			System.out.println(saved);
		}
		
	}

	@Override
	public void onSkipInProcess(StudentCsv item, Throwable throwable) {
		if (throwable instanceof FlatFileParseException) {
			String input = ((FlatFileParseException) throwable).getInput();
			Integer lineNumber = ((FlatFileParseException) throwable).getLineNumber();
			String message = ((FlatFileParseException) throwable).getMessage();
			ErrorEntity errorEntity=new ErrorEntity();
			errorEntity.setInput(input);
			errorEntity.setLineNumber(lineNumber);
			errorEntity.setMessage(message);
			errorEntity.setActionType(BatchImageConstant.READ);
			ErrorEntity saved =errorService.saveErrorRecord(errorEntity);
			System.out.println(saved);
		}
		
	}

	
}
