package com.pool.config.batch.listener;

import com.pool.config.completionpolicy.RandomChunkSizePolicy;
import com.pool.config.listener.CustomChunkAnnotationListener;
import com.pool.config.listener.CustomChunkListener;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.listener.JobListenerFactoryBean;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.repeat.CompletionPolicy;
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
public class ChunkListenerBatchConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    @Bean("chunkListenerJob")
    public Job chunkListenerJob(){
        return new JobBuilder("chunkListenerJob",jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(chunkListenerStep())
                .build();
    }
    @Bean("chunkListenerStep")
    public Step chunkListenerStep(){
        return new StepBuilder("chunkListenerStep",jobRepository)
                .<String,String>chunk(listenerCompletionPolicy(),transactionManager)
                .reader(listItemChunkListenerReader())
                .writer(listItemChunkListenerWriter())
                //.listener(new CustomChunkListener())
                .listener(new CustomChunkAnnotationListener())
                .build();
    }

    @Bean("listItemChunkListenerReader")
    public ListItemReader<String> listItemChunkListenerReader(){
        List<String> items=new ArrayList<>(100000);
        items= IntStream.range(0,100000).mapToObj(operand -> UUID.randomUUID().toString()).collect(Collectors.toList());
        return new ListItemReader<>(items);
    }

    @Bean("listItemChunkListenerWriter")
    public ItemWriter<String> listItemChunkListenerWriter(){
        return chunk -> chunk.getItems().forEach(System.out::println);
    }

    @Bean("listenerCompletionPolicy")
    public CompletionPolicy listenerCompletionPolicy(){
        return new RandomChunkSizePolicy();
    }

}
