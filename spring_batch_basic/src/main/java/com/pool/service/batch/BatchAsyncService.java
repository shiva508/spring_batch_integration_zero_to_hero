package com.pool.service.batch;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.pool.configuration.batch.CustomerCsvFileBatchConfig;
import com.pool.configuration.batch.reader.StudentFlatFileItemReader;
import com.pool.modal.CustomJobParameter;
import com.pool.modal.CustomerModel;

@Service
public class BatchAsyncService {
	
	@Autowired
	private JobLauncher jobLauncher;
	
	@Qualifier("firstJob")
	@Autowired
	private Job firstJob;
	
	@Qualifier("secondJob")
	@Autowired
	private Job secondJob;
	
	@Qualifier("flatFileJob")
	@Autowired
	private Job flatFileJob;
	
	@Qualifier("jsonFileJob")
	@Autowired
	private Job jsonFileJob;
	
	@Qualifier("xmlFileJob")
	@Autowired
	private Job xmlFileJob;
	
	@Qualifier("jdbcStudentJob")
	@Autowired
	private Job jdbcStudentJob;
	
	@Qualifier("serviceStudentJob")
	@Autowired
	private Job serviceStudentJob;
	
	@Qualifier("jdbcJsonStudentJob")
	@Autowired
	private Job jdbcJsonStudentJob;
	
	@Autowired
	private StudentFlatFileItemReader studentFlatFileItemReader;
	
	@Autowired
	private CustomerCsvFileBatchConfig customerCsvFileBatchConfig;
	

	@Async
	public void asyncBatchProsessor(String jobname,List<CustomJobParameter> customJobParameters) {
		try {
			Map<String, JobParameter<?>> param=new HashMap<>();
			param.put("Time", new JobParameter<>(new Date(),Date.class));
			customJobParameters.forEach(customJobParameter->param.put(customJobParameter.getParamKey(), new JobParameter<>(customJobParameter.getParamValue(),String.class)));
			JobParameters jobParameters=new JobParameters(param);
			JobExecution jobExecution=null;
			if(jobname.equals("firstJob")) {
				jobExecution=jobLauncher.run(firstJob, jobParameters);
			}else if(jobname.equals("secondJob")) {
				jobExecution=jobLauncher.run(secondJob, jobParameters);
			}else if(jobname.equals("flatFileJob")) {
				System.out.println("Flat map job");
				jobExecution=jobLauncher.run(flatFileJob, jobParameters);

			}else if(jobname.equals("jsonFileJob")) {
				jobExecution=jobLauncher.run(jsonFileJob, jobParameters);
			}else if(jobname.equals("xmlFileJob")) {
				jobExecution=jobLauncher.run(xmlFileJob, jobParameters);
			}else if(jobname.equals("jdbcStudentJob")) {
				jobExecution=jobLauncher.run(jdbcStudentJob, jobParameters);
			}else if(jobname.equals("serviceStudentJob")) {
				jobExecution=jobLauncher.run(serviceStudentJob, jobParameters);
			}else if(jobname.equals("jdbcJsonStudentJob")) {
				jobExecution=jobLauncher.run(jdbcJsonStudentJob, jobParameters);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	
	public void uploadCsvFile(MultipartFile multipartFile) {
		Map<String, JobParameter<?>> param=new HashMap<>();
		param.put("Time", new JobParameter<>(new Date(),Date.class));
		FlatFileItemReader<CustomerModel> csvCustomerfileItemReader = studentFlatFileItemReader.csvCustomerfileItemReader(multipartFile);
		JobParameters jobParameters=new JobParameters(param);
		JobExecution jobExecution=null;
		try {
			customerCsvFileBatchConfig.setCsvCustomerfileItemReader(csvCustomerfileItemReader);
			jobExecution=jobLauncher.run(customerCsvFileBatchConfig.customerJob(), jobParameters);
		} catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException
				| JobParametersInvalidException e) {
			e.printStackTrace();
		}
		
	}
}
