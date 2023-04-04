package com.pool.configuration.batch.processor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Service;

import com.pool.modal.StudentJdbc;
import com.pool.modal.StudentJdbcJson;

@Service
public class JdbcReaderJsonWriterItemProcessor implements ItemProcessor<StudentJdbc, StudentJdbcJson> {
	@Override
	public StudentJdbcJson process(StudentJdbc studentJdbc) throws Exception {
		StudentJdbcJson studentJdbcJson=new StudentJdbcJson()
															.setId(studentJdbc.getId())
															.setFirstName(studentJdbc.getFirstName())
															.setLastName(studentJdbc.getLastName())
															.setEmail(studentJdbc.getEmail());
		return studentJdbcJson;
	}

}
