package com.pool.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.*;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.FileCopyUtils;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Date;

@Configuration
public class StudentpoolBatchConfig {


    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource){
        return new JdbcTemplate(dataSource);
    }
    @Bean(name = "tasklet")
    @StepScope
    public Tasklet tasklet(@Value("#{jobParameters['date']}") Date date){
        return  (stepContribution, chunkContext) -> {
            System.out.println("Hello World "+date);
            return RepeatStatus.FINISHED;
        };
    }


    @Bean(name = "step")
    public Step step(JobRepository jobRepository,
                      @Qualifier("tasklet") Tasklet tasklet,
                      PlatformTransactionManager transactionManager){
        return new StepBuilder("first-step",jobRepository)
                .tasklet(tasklet,transactionManager)
                .build();
    }
    public Step csvToDb(JobRepository jobRepository,
                        PlatformTransactionManager transactionManager,
                        @Value("/home/shiva/shiva/mywork/assignment/spring_batch_zero_to_hero/data/vgsales.csv")Resource resource){
        var lines=(String[])null;
        try(var reader=new InputStreamReader(resource.getInputStream())) {
            var string= FileCopyUtils.copyToString(reader);
            lines= string.split(System.lineSeparator());
            System.out.println("There are "+lines.length+" lines ");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new StepBuilder("csvToDb",jobRepository).<String,String>chunk(100,transactionManager)
                .reader(new ListItemReader<>(Arrays.asList(lines))).writer(new ItemWriter<String>() {
                    @Override
                    public void write(Chunk<? extends String> chunk) throws Exception {
                        var chunkOneHumdredLines=chunk.getItems();
                        System.out.println(chunkOneHumdredLines);
                    }
                }).build();
    }
    
    @Bean(name = "sampleJob")
    public Job job(JobRepository jobRepository,@Qualifier("step") Step step){
        return new JobBuilder("job",jobRepository)
                                            .start(step)
                                            .build();
    }


}
