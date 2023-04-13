package com.pool.service.error;

import com.pool.entity.ErrorEntity;
import com.pool.repository.ErrorRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
public class ErrorServiceImpl implements ErrorService{

	private final  ErrorRepository errorRepository;
	@Override
	@Transactional
	public ErrorEntity saveErrorRecord(ErrorEntity errorEntity) {
		return errorRepository.saveAndFlush(errorEntity);
	}

}
