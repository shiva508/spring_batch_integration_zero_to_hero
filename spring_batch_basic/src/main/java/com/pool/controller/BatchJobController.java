package com.pool.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.batch.core.launch.JobOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.pool.modal.CustomJobParameter;
import com.pool.modal.StudentResponse;
import com.pool.service.WeWishBatchService;
import com.pool.service.batch.BatchAsyncService;

@RestController
@RequestMapping("/api/job/")
public class BatchJobController {

	@Autowired
	private BatchAsyncService batchAsyncService;

	@Autowired
	private JobOperator jobOperator;

	@Autowired
	private WeWishBatchService weWishBatchService;

	@PostMapping("/start/{jobname}")
	public ResponseEntity<?> batchJobTrigger(@PathVariable("jobname") String jobname,
			@RequestBody List<CustomJobParameter> customJobParameters) {
		try {
			batchAsyncService.asyncBatchProsessor(jobname, customJobParameters);
		} catch (Exception e) {

		}
		return new ResponseEntity<>(jobname, HttpStatus.OK);
	}

	@PostMapping("/stop/{executionid}")
	public ResponseEntity<?> batchJobStopper(@PathVariable("executionid") Long executionid) {
		try {
			jobOperator.stop(executionid);
		} catch (Exception e) {

		}
		return new ResponseEntity<>(executionid, HttpStatus.OK);
	}

	@GetMapping("/studentresponse")
	public ResponseEntity<?> studentsResponse() {

		List<StudentResponse> responses = new ArrayList<>();
		for (long i = 0; i < 10; i++) {
			responses.add(new StudentResponse(i + 1, "Shiva" + i, "Shiva" + i, "datasrshiva" + i + "@gamil.com"));
		}
		return new ResponseEntity<>(responses, HttpStatus.OK);
	}

	@PostMapping("/savestudent")
	public ResponseEntity<StudentResponse> studentResponse(@RequestBody StudentResponse studentResponse) {
		System.out.println("Request received");
		return new ResponseEntity<StudentResponse>(studentResponse, HttpStatus.OK);
	}

	@PostMapping("/uploadcsv")
	public ResponseEntity<?> uploadcsvCustomerFile(@RequestParam("csvfile") MultipartFile multipartFile) {
		batchAsyncService.uploadCsvFile(multipartFile);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping(value = "/laptops")
	public void laptopBatch() {
		weWishBatchService.manualDataBuilder();
	}
}
