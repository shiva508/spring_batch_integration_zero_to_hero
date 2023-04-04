package com.pool.config;

import com.pool.domin.Transaction;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.partition.PartitionHandler;
import org.springframework.batch.core.partition.support.MultiResourcePartitioner;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.MultiResourceItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.deployer.spi.task.TaskLauncher;
import org.springframework.cloud.task.batch.partition.DeployerPartitionHandler;
import org.springframework.cloud.task.batch.partition.DeployerStepExecutionHandler;
import org.springframework.cloud.task.batch.partition.PassThroughCommandLineArgsProvider;
import org.springframework.cloud.task.batch.partition.SimpleEnvironmentVariablesProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class PartitionBatchConfiguration {

    private final JobRepository jobRepository;

    private final PlatformTransactionManager transactionManager;

    private final ConfigurableApplicationContext context;

    public PartitionBatchConfiguration(JobRepository jobRepository,
                              PlatformTransactionManager transactionManager,
                              ConfigurableApplicationContext context) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.context = context;
    }

    @Bean
    public DeployerPartitionHandler partitionHandler(TaskLauncher taskLauncher,
                                                     JobExplorer jobExplorer,
                                                     ApplicationContext context,
                                                     Environment environment) throws Exception {
        Resource resource = context.getResource("file:///Users/mminella/Documents/IntelliJWorkspace/scaling-demos/partitioned-demo/target/partitioned-demo-0.0.1-SNAPSHOT.jar");

        DeployerPartitionHandler partitionHandler = new DeployerPartitionHandler(taskLauncher, jobExplorer, resource, "step1",null);

        List<String> commandLineArgs = new ArrayList<>(3);
        commandLineArgs.add("--spring.profiles.active=worker");
        commandLineArgs.add("--spring.cloud.task.initialize.enable=false");
        commandLineArgs.add("--spring.batch.initializer.enabled=false");
        commandLineArgs.add("--spring.datasource.initialize=false");
        partitionHandler.setCommandLineArgsProvider(new PassThroughCommandLineArgsProvider(commandLineArgs));
        partitionHandler.setEnvironmentVariablesProvider(new SimpleEnvironmentVariablesProvider(environment));
        partitionHandler.setMaxWorkers(3);
        partitionHandler.setApplicationName("PartitionedBatchJobTask");

        return partitionHandler;
    }

    @Bean
    @StepScope
    public MultiResourcePartitioner partitioner(@Value("#{jobParameters['inputFiles']}") Resource[] resources) {
        MultiResourcePartitioner partitioner = new MultiResourcePartitioner();

        partitioner.setKeyName("file");
        partitioner.setResources(resources);

        return partitioner;
    }

    @Bean
    @Profile("worker")
    public DeployerStepExecutionHandler stepExecutionHandler(JobExplorer jobExplorer) {
        return new DeployerStepExecutionHandler(this.context, jobExplorer, this.jobRepository);
    }

    @Bean
    @StepScope
    public FlatFileItemReader<Transaction> fileTransactionReader(
            @Value("#{stepExecutionContext['file']}") Resource resource) {

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
    public JdbcBatchItemWriter<Transaction> writer(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Transaction>()
                .dataSource(dataSource)
                .beanMapped()
                .sql("INSERT INTO TRANSACTION (ACCOUNT, AMOUNT, TIMESTAMP) VALUES (:account, :amount, :timestamp)")
                .build();
    }

    @Bean
    public Step partitionedMaster(PartitionHandler partitionHandler) {
        return new StepBuilder("step1",jobRepository)
                .partitioner(step1().getName(), partitioner(null))
                .step(step1())
                .partitionHandler(partitionHandler)
                .build();
    }

    @Bean
    public Step step1() {
        return new StepBuilder("step1",jobRepository)
                .<Transaction, Transaction>chunk(100,transactionManager)
                .reader(fileTransactionReader(null))
                .writer(writer(null))
                .build();
    }

    @Bean
    @StepScope
    public MultiResourceItemReader<Transaction> multiResourceItemReader(
            @Value("#{jobParameters['inputFiles']}") Resource[] resources) {

        return new MultiResourceItemReaderBuilder<Transaction>()
                .delegate(delegate())
                .name("multiresourceReader")
                .resources(resources)
                .build();
    }

    @Bean
    public FlatFileItemReader<Transaction> delegate() {
        return new FlatFileItemReaderBuilder<Transaction>()
                .name("flatFileTransactionReader")
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
    @Profile("!worker")
    public Job parallelStepsJob() {
        return new JobBuilder("parallelStepsJob",jobRepository)
                .start(partitionedMaster(null))
                .build();
    }

}
