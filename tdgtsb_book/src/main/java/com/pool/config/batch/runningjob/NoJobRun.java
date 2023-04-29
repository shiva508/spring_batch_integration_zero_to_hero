package com.pool.config.batch.runningjob;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@EnableBatchProcessing
@Configuration
@Slf4j
@AllArgsConstructor
public class NoJobRun {

    private final JobRepository jobRepository;

    private final PlatformTransactionManager transactionManager;


}
