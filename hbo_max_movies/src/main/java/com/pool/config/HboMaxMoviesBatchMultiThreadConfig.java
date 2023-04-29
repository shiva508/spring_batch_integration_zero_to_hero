package com.pool.config;

import com.pool.config.listener.TitleBadRecordListener;
import com.pool.config.writer.HboCreditWriter;
import com.pool.config.writer.HboTitleWriter;
import com.pool.entity.CreditEntity;
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
import org.springframework.batch.core.step.skip.AlwaysSkipItemSkipPolicy;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@EnableBatchProcessing
@Configuration
@Slf4j
@AllArgsConstructor
public class HboMaxMoviesBatchMultiThreadConfig {

    private final JobRepository jobRepository;

    private final PlatformTransactionManager platformTransactionManager;

    private final TitleBadRecordListener titleBadRecordListener;

    private final HboTitleWriter hboTitleWriter;

    private final HboCreditWriter hboCreditWriter;

    @Bean("hboTitleMultiThreadJob")
    public Job hboTitleMultiThreadJob(){
        return new JobBuilder("hboTitleMultiThreadJob",jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(hboCreditMultiThreadStep())
                .build();
    }

    @Bean("hboCreditMultiThreadStep")
    public Step hboCreditMultiThreadStep(){
        ThreadPoolTaskExecutor hboTaskExecutor=new ThreadPoolTaskExecutor();
        hboTaskExecutor.setCorePoolSize(6);
        hboTaskExecutor.setMaxPoolSize(6);
        hboTaskExecutor.afterPropertiesSet();
        return new StepBuilder("hboCreditMultiThreadStep",jobRepository)
                .<CreditEntity,CreditEntity>chunk(1000,platformTransactionManager)
                .reader(hboCreditMultiThreadReader(null))
                .writer(hboCreditWriter)
                .faultTolerant()
                .skip(Throwable.class)
                .skip(FlatFileParseException.class)
                .skipLimit(Integer.MAX_VALUE)
                .skipPolicy(new AlwaysSkipItemSkipPolicy())
                .taskExecutor(hboTaskExecutor)
                .listener(titleBadRecordListener)
                .build();
    }

    @Bean("hboCreditMultiThreadReader")
    @StepScope
    public FlatFileItemReader<CreditEntity> hboCreditMultiThreadReader(@Value("${hbo.credit.file.path}") Resource resource){
        return new FlatFileItemReaderBuilder<CreditEntity>()
                .name("hboCreditReader")
                .linesToSkip(1)
                .resource(resource)
                .delimited().delimiter(",")
                .names(new String[]{"person_id",
                                    "id",
                                    "name",
                                    "character",
                                    "role"})
                .fieldSetMapper(fieldSet -> CreditEntity.builder()
                        .personId(fieldSet.readString("person_id"))
                        .movieId(fieldSet.readString("id"))
                        .name(fieldSet.readString("name"))
                        .character(fieldSet.readString("character"))
                        .role(fieldSet.readString("role"))
                        .build())
                .build();
    }

}
