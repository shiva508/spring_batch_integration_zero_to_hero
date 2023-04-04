package com.pool.config.batch.jobmetadata;

import com.pool.config.tasklet.jobmetadata.JobMetadataEndTasklet;
import com.pool.config.tasklet.jobmetadata.JobMetadataStartTasklet;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.listener.ExecutionContextPromotionListener;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@EnableBatchProcessing
@Configuration
@Slf4j
@AllArgsConstructor
public class JobMetadataBatchConfig {

    private final JobRepository jobRepository;

    private final PlatformTransactionManager transactionManager;

    private final JobExplorer jobExplorer;

    @Bean("jobMetadataStartStep")
    public Step jobMetadataStartStep(){
        return new StepBuilder("jobMetadataStartStep",jobRepository)
                .tasklet(new JobMetadataStartTasklet(jobExplorer),transactionManager)
                //.listener(new JobMetadataStartTasklet(jobExplorer))
                .listener(metaDataExecutionContextPromotionListener())
                .build();
    }
    @Bean("jobMetadataStartEnd")
    public Step jobMetadataStartEnd(){
        return new StepBuilder("jobMetadataStartEnd",jobRepository)
                .tasklet(new JobMetadataEndTasklet(jobExplorer),transactionManager)
                //.listener(new JobMetadataEndTasklet(jobExplorer))
                .listener(metaDataExecutionContextPromotionListener())
                .build();
    }

    @Bean("jobMetadataJob")
    public Job jobMetadataJob(){
        return new JobBuilder("jobMetadataJob",jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(jobMetadataStartStep())
                .next(jobMetadataStartEnd())
                //.listener(metaDataExecutionContextPromotionListener())
                .build();
    }

    @Bean("metaDataExecutionContextPromotionListener")
    public ExecutionContextPromotionListener metaDataExecutionContextPromotionListener(){
        ExecutionContextPromotionListener promotionListener=new ExecutionContextPromotionListener();
        promotionListener.setKeys(new String[]{"JOB_ID"});
       // promotionListener.setStrict(true);
        return promotionListener;
    }

}
