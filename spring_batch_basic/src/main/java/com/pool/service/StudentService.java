package com.pool.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.pool.modal.StudentResponse;

@Service
public class StudentService {

	List<StudentResponse> collect;

	public List<StudentResponse> studentResponseProcessor() {
		RestTemplate restTemplate = new RestTemplate();
		StudentResponse[] forObject = restTemplate.getForObject("http://localhost:8081/api/job/studentresponse",
				StudentResponse[].class);
		collect = Stream.of(forObject).collect(Collectors.toList());
		return collect;
	}

	public StudentResponse studentResponse() {
		if (collect == null) {
			studentResponseProcessor();
		}

		if (collect != null && !collect.isEmpty()) {
			return collect.remove(0);
		}
		return null;
	}
	
	public StudentResponse studentResponse(String name) {
		if (collect == null) {
			studentResponseProcessor();
		}

		if (collect != null && !collect.isEmpty()) {
			return collect.remove(0);
		}
		return null;
	}
	
	public StudentResponse saveStudentRequest(StudentResponse studentRequest) {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.postForObject("http://localhost:8081/api/job/savestudent",studentRequest,StudentResponse.class);
		return studentRequest;
	}
}
