package com.pool.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing
public class VideoGameCsvToDbJobConfig {

    public static final String EMPTY_CSV_STATUS = "EMPTY";
    @Bean(name = "videoGameJob")
    public Job videoGameJob(JobRepository jobRepository,
                            @Qualifier("videoGameStep") Step videoGameStep,
                            YearPlatformReportStepConfiguration yearPlatformReportStepConfiguration,
                            YearReportStepConfiguration yearReportStepConfiguration,
                            EndStepConfiguration endStepConfiguration,
                            ErrorStepConfiguration errorStepConfiguration){
        return new JobBuilder("videoGameJob",jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(videoGameStep).on(EMPTY_CSV_STATUS).to(errorStepConfiguration.errorStep())
                .from(videoGameStep).on("*").to(yearPlatformReportStepConfiguration.yearPlatformReportStep())
                .next(yearReportStepConfiguration.yearReportStep())
                .next(endStepConfiguration.jobFinidhedStep())
                .build()
                .build();
    }
}
