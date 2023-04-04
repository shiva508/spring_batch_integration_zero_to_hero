package com.pool.config.batch.chunk;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.item.support.ListItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@EnableBatchProcessing
@Configuration
@Slf4j
@AllArgsConstructor
public class ChuckSizeBatchConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean("chunkSizeJob")
    public Job chunkSizeJob(){
        return new JobBuilder("chunkSizeJob",jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(chunkSizeStep())
                .build();
    }
    @Bean("chunkSizeStep")
    public Step chunkSizeStep(){
        return new StepBuilder("chunkSizeStep",jobRepository)
                .<String,String>chunk(1000,transactionManager)
                .reader(listItemChunkSizeReader())
                .writer(listItemChunkSizeWriter())
                .build();
    }

    @Bean("listItemChunkSizeReader")
    public ListItemReader<String> listItemChunkSizeReader(){
        List<String> items=new ArrayList<>(100000);
        items=IntStream.range(0,100000).mapToObj(operand -> UUID.randomUUID().toString()).collect(Collectors.toList());
        return new ListItemReader<>(items);
    }

    @Bean("listItemChunkSizeWriter")
    public ItemWriter<String> listItemChunkSizeWriter(){
        return chunk -> chunk.getItems().forEach(System.out::println);
    }
}
