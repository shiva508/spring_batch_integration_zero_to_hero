package com.pool.config.listener;

import com.pool.entity.ErrorEntity;
import com.pool.service.error.ErrorService;
import com.pool.util.HboConstants;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.annotation.OnSkipInProcess;
import org.springframework.batch.core.annotation.OnSkipInRead;
import org.springframework.batch.core.annotation.OnSkipInWrite;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class TitleBadRecordListener {

    private final ErrorService errorService;

    @OnSkipInRead
    public void onReadBadRecordFound(Throwable throwable) {
        if (throwable instanceof FlatFileParseException) {
            String input = ((FlatFileParseException) throwable).getInput();
            Integer lineNumber = ((FlatFileParseException) throwable).getLineNumber();
            String message = throwable.getMessage();
            ErrorEntity errorEntity=new ErrorEntity();
            if(null !=input && input.length()>255){
                errorEntity.setInput(input.substring(0,254));
            }else{
                errorEntity.setInput(input);
            }

            if(null !=message && message.length()>255){
                errorEntity.setMessage(message.substring(0,254));
            }else {
                errorEntity.setMessage(message);
            }
            errorEntity.setLineNumber(lineNumber);
            errorEntity.setActionType(HboConstants.READ);
            ErrorEntity saved =errorService.saveErrorRecord(errorEntity);
            log.info("onReadBadRecordFound={}",saved);
        }
    }

    @OnSkipInProcess
    public void onProcessBadRecordFound(Object titleEntity,Throwable throwable) {
        if (throwable instanceof FlatFileParseException) {
            String input = ((FlatFileParseException) throwable).getInput();
            Integer lineNumber = ((FlatFileParseException) throwable).getLineNumber();
            String message = throwable.getMessage();
            ErrorEntity errorEntity=new ErrorEntity();
            if(null !=input && input.length()>255){
                errorEntity.setInput(input.substring(0,254));
            }else{
                errorEntity.setInput(input);
            }

            if(null !=message && message.length()>255){
                errorEntity.setMessage(message.substring(0,254));
            }else {
                errorEntity.setMessage(message);
            }
            errorEntity.setLineNumber(lineNumber);
            errorEntity.setActionType(HboConstants.READ);
            ErrorEntity saved =errorService.saveErrorRecord(errorEntity);
            log.info("onProcessBadRecordFound={}",saved);
        }
    }

    @OnSkipInWrite
    public void onWriteBadRecordFound(Object titleEntity, Throwable throwable) {
        if (throwable instanceof FlatFileParseException) {
            String input = ((FlatFileParseException) throwable).getInput();
            Integer lineNumber = ((FlatFileParseException) throwable).getLineNumber();
            String message = throwable.getMessage();
            ErrorEntity errorEntity=new ErrorEntity();
            if(null !=input && input.length()>255){
                errorEntity.setInput(input.substring(0,254));
            }else{
                errorEntity.setInput(input);
            }

            if(null !=message && message.length()>255){
                errorEntity.setMessage(message.substring(0,254));
            }else {
                errorEntity.setMessage(message);
            }
            errorEntity.setLineNumber(lineNumber);
            errorEntity.setActionType(HboConstants.READ);
            ErrorEntity saved =errorService.saveErrorRecord(errorEntity);
            log.info("onWriteBadRecordFound={}",saved);
        }
    }

}
