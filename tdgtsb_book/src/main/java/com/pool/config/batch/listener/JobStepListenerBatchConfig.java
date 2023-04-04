package com.pool.config.batch.listener;

import com.pool.config.incrementer.CustomRunIdIncrementer;
import com.pool.config.listener.CustomJobExecutionAnnotationListener;
import com.pool.config.listener.CustomStepExecutionListener;
import com.pool.config.validator.CustomParameterValidator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.CompositeJobParametersValidator;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.listener.JobListenerFactoryBean;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Arrays;

@EnableBatchProcessing
@Configuration
@Slf4j
@AllArgsConstructor
public class JobStepListenerBatchConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;


    @Bean("listenerStep")
    public Step listenerStep(){
        return new StepBuilder("listenerStep",jobRepository)
                .tasklet(taskletListenerBean(null,null),transactionManager)
                .listener(new CustomStepExecutionListener())
                .build();
    }
    @Bean("listenerJob")
    public Job listenerJob(){
        return new JobBuilder("listenerJob",jobRepository)
                .incrementer(new CustomRunIdIncrementer())
                .start(listenerStep())
                .validator(jobListenerValidator())
                //.listener(new CustomJobExecutionListner())
                .listener(JobListenerFactoryBean.getListener(new CustomJobExecutionAnnotationListener()))
                .build();
    }

    @Bean
    @StepScope
    public Tasklet taskletListenerBean(@Value("#{jobParameters['jobTriggeredDate']}") String date,
                                               @Value("#{jobParameters['csvFilePath']}") String csvFilePath){
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                log.info("jobTriggeredDate={},csvFilePath={}",date,csvFilePath);
                return RepeatStatus.FINISHED;
            }
        };
    }

    @Bean
    public CompositeJobParametersValidator jobListenerValidator(){
        CompositeJobParametersValidator compositeJobParametersValidator=new CompositeJobParametersValidator();
        DefaultJobParametersValidator parametersValidator=new DefaultJobParametersValidator();
        parametersValidator.setRequiredKeys(new String[]{"csvFilePath"});
        parametersValidator.setOptionalKeys(new String[]{"jobTriggeredDate","customRunId"});
        parametersValidator.afterPropertiesSet();
        compositeJobParametersValidator.setValidators(Arrays.asList(new CustomParameterValidator(),parametersValidator));
        return compositeJobParametersValidator;
    }

}
