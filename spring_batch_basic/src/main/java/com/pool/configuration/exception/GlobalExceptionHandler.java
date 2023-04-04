package com.pool.configuration.exception;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.pool.model.exception.CustomErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(value = NoRecardsFoundException.class)
	public ResponseEntity<CustomErrorResponse> handleGenericNotFoundException(NoRecardsFoundException error){
		return new ResponseEntity<CustomErrorResponse>(new CustomErrorResponse()
															.setErrorCode(HttpStatus.NOT_FOUND.toString())
															.setErrorMsg(error.getMessage())
															.setStatus((HttpStatus.NOT_FOUND.value()))
															.setTimestamp(new Date()), HttpStatus.NOT_FOUND);
	}
}
