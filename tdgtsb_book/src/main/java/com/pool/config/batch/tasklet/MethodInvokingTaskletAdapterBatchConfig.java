package com.pool.config.batch.tasklet;

import com.pool.service.IplService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.MethodInvokingTaskletAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@EnableBatchProcessing
@Configuration
@Slf4j
@AllArgsConstructor
public class MethodInvokingTaskletAdapterBatchConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean("methodInvokingJob")
    public Job methodInvokingJob(){
        return new JobBuilder("methodInvokingJob",jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(methodInvokingStep())
                .build();
    }

    @Bean("methodInvokingStep")
    public Step methodInvokingStep(){
        return new StepBuilder("methodInvokingStep",jobRepository)
                .tasklet(methodInvokingTaskletAdapter(),transactionManager)
                .build();
    }

    @Bean("methodInvokingTaskletAdapter")
    public MethodInvokingTaskletAdapter methodInvokingTaskletAdapter(){
        MethodInvokingTaskletAdapter taskletAdapter=new MethodInvokingTaskletAdapter();
        taskletAdapter.setTargetObject(iplService());
        taskletAdapter.setTargetMethod("iplScheduleInfo");
        return taskletAdapter;
    }
    @Bean("iplService")
    public IplService iplService(){
        return new IplService();
    }
}
