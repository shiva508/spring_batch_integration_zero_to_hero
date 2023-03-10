package com.pool.config;

import javax.sql.DataSource;

import com.pool.domin.Transaction;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.item.xml.builder.StaxEventItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class ParallelStepsJobApplication {

    private final JobRepository jobRepository;

    private final PlatformTransactionManager transactionManager;

    public ParallelStepsJobApplication(JobRepository jobRepository,
                                       PlatformTransactionManager transactionManager) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
    }

    @Bean
    public Job parallelStepsJob() {
        Flow secondFlow = new FlowBuilder<Flow>("secondFlow")
                .start(step2())
                .build();

        Flow parallelFlow = new FlowBuilder<Flow>("parallelFlow")
                .start(step1())
                .split(new SimpleAsyncTaskExecutor())
                .add(secondFlow)
                .build();

        return new JobBuilder("parallelStepsJob",jobRepository)
                .start(parallelFlow)
                .end()
                .build();
    }

//	@Bean
//	public Job sequentialStepsJob() {
//		return this.jobBuilderFactory.get("sequentialStepsJob")
//				.start(step1())
//				.next(step2())
//				.build();
//	}

    @Bean
    @StepScope
    public FlatFileItemReader<Transaction> fileTransactionReader(
            @Value("#{jobParameters['inputFlatFile']}") Resource resource) {

        return new FlatFileItemReaderBuilder<Transaction>()
                .name("flatFileTransactionReader")
                .resource(resource)
                .delimited()
                .names(new String[] {"account", "amount", "timestamp"})
                .fieldSetMapper(fieldSet -> {
                    Transaction transaction = new Transaction();

                    transaction.setAccount(fieldSet.readString("account"));
                    transaction.setAmount(fieldSet.readBigDecimal("amount"));
                    transaction.setTimestamp(fieldSet.readDate("timestamp", "yyyy-MM-dd HH:mm:ss"));

                    return transaction;
                })
                .build();
    }

    @Bean
    @StepScope
    public StaxEventItemReader<Transaction> xmlTransactionReader(
            @Value("#{jobParameters['inputXmlFile']}") Resource resource) {
        Jaxb2Marshaller unmarshaller = new Jaxb2Marshaller();
        unmarshaller.setClassesToBeBound(Transaction.class);

        return new StaxEventItemReaderBuilder<Transaction>()
                .name("xmlFileTransactionReader")
                .resource(resource)
                .addFragmentRootElements("transaction")
                .unmarshaller(unmarshaller)
                .build();
    }

    @Bean
    @StepScope
    public JdbcBatchItemWriter<Transaction> writer(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Transaction>()
                .dataSource(dataSource)
                .beanMapped()
                .sql("INSERT INTO TRANSACTION (ACCOUNT, AMOUNT, TIMESTAMP) VALUES (:account, :amount, :timestamp)")
                .build();
    }

    @Bean
    public Step step1() {
        return new StepBuilder("step1",jobRepository)
                .<Transaction, Transaction>chunk(100,transactionManager)
                .reader(xmlTransactionReader(null))
                .writer(writer(null))
                .build();
    }

    @Bean
    public Step step2() {
        return new StepBuilder("step2",jobRepository)
                .<Transaction, Transaction>chunk(100,transactionManager)
                .reader(fileTransactionReader(null))
                .writer(writer(null))
                .build();
    }

    public static void main(String[] args) {
        String [] newArgs = new String[] {"inputFlatFile=/data/csv/bigtransactions.csv",
                "inputXmlFile=/data/xml/bigtransactions.xml"};

        SpringApplication.run(ParallelStepsJobApplication.class, newArgs);
    }
}