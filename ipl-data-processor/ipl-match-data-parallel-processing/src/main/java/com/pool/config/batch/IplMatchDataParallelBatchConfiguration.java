package com.pool.config.batch;

import com.pool.model.TransactionObj;
import com.pool.record.Transaction;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.item.xml.builder.StaxEventItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Date;

@Configuration
public class IplMatchDataParallelBatchConfiguration {

    public static final Date date=new Date();

    private final JobRepository jobRepository;

    private final PlatformTransactionManager transactionManager;

    public IplMatchDataParallelBatchConfiguration(JobRepository jobRepository,
                                                  PlatformTransactionManager transactionManager) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
    }


    @Bean
    public Job iplJob(){
        return new JobBuilder("iplJob"+date,jobRepository)
                .start(flowOne())
                .end()
                .build();
    }

    @Bean
    public Flow flowOne(){
        System.out.println("<========================flowOne===========================>");
        return new FlowBuilder<Flow>("flowOne"+ date)
                .start(stepOne())
                .split(new SimpleAsyncTaskExecutor())
                .add(flowTwo())
                .build();
    }
    @Bean
    public Flow flowTwo(){
        System.out.println("<========================flowTwo===========================>");
        return new FlowBuilder<Flow>("flowTwo"+ date)
                .start(stepTwo())
                .build();
    }

    @Bean("stepOne")
    public Step stepOne(){
        return new StepBuilder("stepOne"+date,jobRepository)
                .<Transaction,Transaction>chunk(100,transactionManager)
                .reader(csvTransactionReader(null))
                .writer(chunk -> chunk.getItems().forEach(System.out::println))
                .build();
    }

    @Bean("stepTwo")
    public Step stepTwo(){
        return new StepBuilder("stepTwo"+date,jobRepository)
                .<TransactionObj,TransactionObj>chunk(100,transactionManager)
                .reader(xmlTransactionReader(null))
                .writer(chunk -> chunk.getItems().forEach(System.out::println))
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<Transaction> csvTransactionReader(@Value("${transaction.csv.file.path}")Resource resource){
        return new FlatFileItemReaderBuilder<Transaction>()
                .resource(resource)
                .name("csvTransactionReader"+ date)
                .delimited().delimiter(",")
                .names("account,amount,timestamp".split(","))
                .fieldSetMapper(fieldSet -> new Transaction(fieldSet.readString("account"),
                                                            fieldSet.readDate("timestamp", "yyyy-MM-dd HH:mm:ss"),
                                                            fieldSet.readBigDecimal("amount")))
                .build();

    }

    @Bean
    @StepScope
    public StaxEventItemReader<TransactionObj> xmlTransactionReader(@Value("${transaction.xml.file.path}")Resource resource){
        Jaxb2Marshaller unmarshaller = new Jaxb2Marshaller();
        unmarshaller.setClassesToBeBound(TransactionObj.class);
        return new StaxEventItemReaderBuilder<TransactionObj>()
                .name("xmlTransactionReader"+date)
                .resource(resource)
                .addFragmentRootElements("transaction")
                .unmarshaller(unmarshaller)
                .build();
    }
    @Bean
    public TaskExecutor iplTaskExecutor(){
        return new SimpleAsyncTaskExecutor("iplTaskExecutor"+ date);
    }
}
