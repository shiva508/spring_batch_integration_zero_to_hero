package com.pool.config.batch.chunk;

import com.pool.config.completionpolicy.RandomChunkSizePolicy;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.repeat.CompletionPolicy;
import org.springframework.batch.repeat.policy.CompositeCompletionPolicy;
import org.springframework.batch.repeat.policy.SimpleCompletionPolicy;
import org.springframework.batch.repeat.policy.TimeoutTerminationPolicy;
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
public class RandomChunkSizePolicyChinkBatchConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean("chunkRandomCompletionJob")
    public Job chunkRandomCompletionJob(){
        return new JobBuilder("chunkRandomCompletionJob",jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(chunkRandomCompletionStep())
                .build();
    }
    @Bean("chunkRandomCompletionStep")
    public Step chunkRandomCompletionStep(){
        return new StepBuilder("chunkRandomCompletionStep",jobRepository)
                .<String,String>chunk(randomCompletionPolicy(),transactionManager)
                .reader(listItemChunkRandomCompletionReader())
                .writer(listItemChunkRandomCompletionWriter())
                .build();
    }

    @Bean("listItemChunkRandomCompletionReader")
    public ListItemReader<String> listItemChunkRandomCompletionReader(){
        List<String> items=new ArrayList<>(100000);
        items= IntStream.range(0,100000).mapToObj(operand -> UUID.randomUUID().toString()).collect(Collectors.toList());
        return new ListItemReader<>(items);
    }

    @Bean("listItemChunkRandomCompletionWriter")
    public ItemWriter<String> listItemChunkRandomCompletionWriter(){
        return chunk -> chunk.getItems().forEach(System.out::println);
    }

    @Bean("randomCompletionPolicy")
    public CompletionPolicy randomCompletionPolicy(){
        return new RandomChunkSizePolicy();
    }

}
