package com.pool.configuration.batch.listener;

import java.io.File;
import java.io.FileWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.annotation.OnSkipInProcess;
import org.springframework.batch.core.annotation.OnSkipInRead;
import org.springframework.batch.core.annotation.OnSkipInWrite;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.pool.domin.ErrorEntity;
import com.pool.modal.StudentCsv;
import com.pool.service.WishFriendService;
import com.pool.service.error.ErrorService;
import com.pool.util.BatchImageConstant;

@Service
@Slf4j
public class SkipBadRecordListener {

	@Autowired
	private ErrorService errorService;
	@Autowired
	private WishFriendService wishFriendService;

	@OnSkipInRead
	//@Transactional
	public void onReadBadRecordFound(Throwable throwable) {
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
			log.info("onReadBadRecordFound={}",saved);
		}
	}
	
	@OnSkipInProcess
	public void onProcessBadRecordFound(StudentCsv studentCsv,Throwable throwable) {
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
			log.info("onProcessBadRecordFound={}",saved);
		}
	}
	
	@OnSkipInWrite
	public void onWriteBadRecordFound(StudentCsv studentCsv,Throwable throwable) {
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
			log.info("onWriteBadRecordFound={}",saved);
		}
	}


	public void createFile(String fileName, String data) {
		try (FileWriter fileWriter = new FileWriter(new File(fileName), true)) {
			fileWriter.write(data + " \n");
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
