package com.pool.config.batch.tasklet;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.SimpleSystemProcessExitCodeMapper;
import org.springframework.batch.core.step.tasklet.SystemCommandTasklet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@EnableBatchProcessing
@Configuration
@Slf4j
@AllArgsConstructor
public class SystemCommandTaskletBatchConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean("systemCommandJob")
    public Job systemCommandJob(){
        return new JobBuilder("systemCommandJob",jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(systemCommandStep())
                .build();
    }

    @Bean("systemCommandStep")
    public Step systemCommandStep(){
        return new StepBuilder("systemCommandStep",jobRepository)
                .tasklet(systemCommandTasklet(),transactionManager)
                .build();
    }

    @Bean
    public SystemCommandTasklet systemCommandTasklet(){
        SystemCommandTasklet systemCommandTasklet=new SystemCommandTasklet();
        systemCommandTasklet.setWorkingDirectory("/home/shiva/Desktop/output");
        systemCommandTasklet.setCommand("ls");
        systemCommandTasklet.setTerminationCheckInterval(5000);
        systemCommandTasklet.setTimeout(5000);
        systemCommandTasklet.setInterruptOnCancel(true);
        systemCommandTasklet.setSystemProcessExitCodeMapper(simpleSystemProcessExitCodeMapper());
        systemCommandTasklet.setTaskExecutor(new SimpleAsyncTaskExecutor());
        return systemCommandTasklet;
    }

    @Bean
    public SimpleSystemProcessExitCodeMapper simpleSystemProcessExitCodeMapper(){
        return new SimpleSystemProcessExitCodeMapper();
    }
}
