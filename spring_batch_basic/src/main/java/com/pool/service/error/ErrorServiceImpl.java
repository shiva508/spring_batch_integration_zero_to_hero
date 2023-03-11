package com.pool.service.error;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.pool.domin.ErrorEntity;
import com.pool.repository.ErrorRepository;

@Service
public class ErrorServiceImpl implements ErrorService{
	
	@Autowired
	private ErrorRepository errorRepository;

	@Override
	@Transactional
	public ErrorEntity saveErrorRecord(ErrorEntity errorEntity) {
		return errorRepository.saveAndFlush(errorEntity);
	}

}
