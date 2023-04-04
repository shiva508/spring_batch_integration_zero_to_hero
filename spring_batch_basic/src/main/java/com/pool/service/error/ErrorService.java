package com.pool.service.error;

import com.pool.domin.ErrorEntity;

public interface ErrorService {
	public ErrorEntity saveErrorRecord(ErrorEntity errorEntity);
}
