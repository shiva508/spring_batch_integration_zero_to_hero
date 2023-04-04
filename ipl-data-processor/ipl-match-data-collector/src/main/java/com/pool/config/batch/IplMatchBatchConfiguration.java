package com.pool.config.batch;

import com.pool.record.Match;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.*;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

//@Configuration
public class IplMatchBatchConfiguration {

    private final JobRepository jobRepository;

    private final PlatformTransactionManager transactionManager;

    public IplMatchBatchConfiguration(JobRepository jobRepository,
                                      PlatformTransactionManager transactionManager) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
    }

    @Bean
    public Job iplMatchjob(@Qualifier("iplMatchStep") Step iplMatchStep){
        return new JobBuilder("iplMatchjob",jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(iplMatchStep)
                .build();
    }
    @Bean("iplMatchStep")
    public Step iplMatchStep(@Qualifier("ipcFileItemReader")ItemReader<Match> itemReader,
                             TaskExecutor iplDataTaskExecutor){
        return new StepBuilder("iplMatchStep",jobRepository)
                .<Match,Match>chunk(100,transactionManager)
                .reader(itemReader)
                .writer(chunk -> chunk.getItems().forEach(System.out::println))
                .taskExecutor(iplDataTaskExecutor)
                .build();
    }

    @Bean("ipcFileItemReader")
    public FlatFileItemReader<Match> ipcFileItemReader(@Value("${csv.file.path}") Resource resource) {
        return new FlatFileItemReaderBuilder<Match>()
                .resource(resource)
                .name("ipcFileItemReader")
                .delimited().delimiter(",")
                .names("id,season,city,date,team1,team2,toss_winner,toss_decision,result,dl_applied,winner,win_by_runs,win_by_wickets,player_of_match,venue,umpire1,umpire2,umpire3".split(","))
                .linesToSkip(1)
                .fieldSetMapper(fieldSet -> new Match(
                        fieldSet.readString("id"),
                        fieldSet.readString("season"),
                        fieldSet.readString("city"),
                        fieldSet.readString("date"),
                        fieldSet.readString("team1"),
                        fieldSet.readString("team2"),
                        fieldSet.readString("toss_winner"),
                        fieldSet.readString("toss_decision"),
                        fieldSet.readString("result"),
                        fieldSet.readString("dl_applied"),
                        fieldSet.readString("winner"),
                        fieldSet.readString("win_by_runs"),
                        fieldSet.readString("win_by_wickets"),
                        fieldSet.readString("player_of_match"),
                        fieldSet.readString("venue"),
                        fieldSet.readString("umpire1"),
                        fieldSet.readString("umpire2"),
                        fieldSet.readString("umpire3")
                )).build();
    }

    @Bean
    public TaskExecutor iplDataTaskExecutor(){
        return new SimpleAsyncTaskExecutor("Ipl-Batch_Data-");
    }

}
