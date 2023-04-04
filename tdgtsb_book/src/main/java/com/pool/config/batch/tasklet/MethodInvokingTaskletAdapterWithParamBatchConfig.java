package com.pool.config.batch.tasklet;

import com.pool.service.IplService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.MethodInvokingTaskletAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@EnableBatchProcessing
@Configuration
@Slf4j
@AllArgsConstructor
public class MethodInvokingTaskletAdapterWithParamBatchConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean("methodInvokingParamJob")
    public Job methodInvokingParamJob(){
        return new JobBuilder("methodInvokingParamJob",jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(methodInvokingStep())
                .build();
    }

    @Bean("methodInvokingParamStep")
    public Step methodInvokingStep(){
        return new StepBuilder("methodInvokingParamStep",jobRepository)
                .tasklet(methodInvokingTaskletAdapterParam(null),transactionManager)
                .build();
    }

    @Bean("methodInvokingTaskletAdapterParam")
    @StepScope
    public MethodInvokingTaskletAdapter methodInvokingTaskletAdapterParam(@Value("#{jobParameters['username']}") String username){
        MethodInvokingTaskletAdapter methodInvokingTaskletAdapter=new MethodInvokingTaskletAdapter();
        methodInvokingTaskletAdapter.setTargetObject(iplServiceParam());
        methodInvokingTaskletAdapter.setTargetMethod("iplUserName");
        methodInvokingTaskletAdapter.setArguments(new String[]{username});
        return methodInvokingTaskletAdapter;
    }

    @Bean("iplServiceParam")
    public IplService iplServiceParam(){
        return new IplService();
    }
}
