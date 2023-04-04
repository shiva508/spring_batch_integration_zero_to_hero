package com.pool.config.batch.chunk;

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
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.mapping.PassThroughLineMapper;
import org.springframework.batch.item.file.transform.PassThroughLineAggregator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;
import org.springframework.transaction.PlatformTransactionManager;

@EnableBatchProcessing
@Configuration
@Slf4j
@AllArgsConstructor
public class ChuckBatchConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean("chunkJob")
    public Job chunkJob(){
        return new JobBuilder("chunkJob",jobRepository)
                .start(chunkStep())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean("chunkStep")
    public Step chunkStep(){
        return new StepBuilder("chunkStep",jobRepository)
                .<String,String>chunk(12,transactionManager)
                .reader(flatFileItemChunkReader(null))
                .writer(flatFileItemChunkWriter(null))
                .build();
    }

    @Bean("flatFileItemChunkReader")
    @StepScope
    public FlatFileItemReader<String> flatFileItemChunkReader(@Value("#{jobParameters['inputFile']}")Resource inputFile){
        return new FlatFileItemReaderBuilder<String>()
                .name("fileItemChunkReader")
                .resource(inputFile)
                .lineMapper(new PassThroughLineMapper())
                .build();
    }

    @Bean("flatFileItemChunkWriter")
    @StepScope
    public FlatFileItemWriter<String> flatFileItemChunkWriter(@Value("#{jobParameters['outputFile']}") WritableResource outputFile ){
        return new FlatFileItemWriterBuilder<String>()
                .name("flatFileItemChunkWriter")
                .resource(outputFile)
                .lineAggregator(new PassThroughLineAggregator<>())
                .build();
    }
}
