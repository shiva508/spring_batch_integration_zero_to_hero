package com.pool.config.batch;

import com.pool.props.TweetCsvConfiguration;
import com.pool.record.Transaction;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.partition.PartitionHandler;
import org.springframework.batch.core.partition.support.MultiResourcePartitioner;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
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
import org.springframework.cloud.task.configuration.EnableTask;
import org.springframework.cloud.task.repository.TaskRepository;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//@Configuration
//@EnableTask
public class IplMatchDataPartitionBatchCoonfiguration {

    private final JobRepository jobRepository;

    private final PlatformTransactionManager transactionManager;

    private final ConfigurableApplicationContext context;



    @Value("file://${HOME}/shiva/mywork/assignment/dada/spring_batch_integration_zero_to_hero/data/partition/csv/transactions*.csv")
    private Resource[] inputResources;

    public IplMatchDataPartitionBatchCoonfiguration(JobRepository jobRepository,
                                       PlatformTransactionManager transactionManager,
                                       ConfigurableApplicationContext context) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.context = context;
    }

    /*@Bean
    @Profile("!worker")
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
    }*/

    @Bean
    @StepScope
    public MultiResourcePartitioner partitioner() {
        System.out.println("=================>MultiResourcePartitioner<===============================");
        MultiResourcePartitioner partitioner = new MultiResourcePartitioner();
        partitioner.setKeyName("file");
        partitioner.setResources(inputResources);
        return partitioner;
    }

    /*@Bean
    @Profile("worker")
    public DeployerStepExecutionHandler stepExecutionHandler(JobExplorer jobExplorer) {
        return new DeployerStepExecutionHandler(this.context, jobExplorer, this.jobRepository);
    }*/

    @Bean
    @StepScope
    public FlatFileItemReader<Transaction> fileTransactionReader(
            @Value("#{stepExecutionContext['file']}") Resource resource) {
        System.out.println("=================>fileTransactionReader<===============================");
        System.out.println(resource.getFilename());
        return new FlatFileItemReaderBuilder<Transaction>()
                .name("flatFileTransactionReader")
                .resource(resource)
                .delimited()
                .names(new String[] {"account", "amount", "timestamp"})
                .fieldSetMapper(fieldSet -> new Transaction(
                        fieldSet.readString("account"),
                        fieldSet.readDate("timestamp", "yyyy-MM-dd HH:mm:ss"),
                        fieldSet.readBigDecimal("amount"))
                ).build();
    }

    @Bean
    public PartitionHandler partitionHandler() {
        System.out.println("===================>partitionHandler<===============================");
        TaskExecutorPartitionHandler retVal = new TaskExecutorPartitionHandler();
        retVal.setTaskExecutor(taskExecutor());
        retVal.setStep(step1());
        retVal.setGridSize(10);
        return retVal;
    }

    @Bean
    public TaskExecutor taskExecutor() {
        return new SimpleAsyncTaskExecutor("spring_batch"+new Date());
    }
    @Bean
    @StepScope
    public JdbcBatchItemWriter<Transaction> writer(DataSource dataSource) {
        System.out.println("===================>writer<===============================");
        return new JdbcBatchItemWriterBuilder<Transaction>()
                .dataSource(dataSource)
                .beanMapped()
                .sql("INSERT INTO TRANSACTION (ACCOUNT, AMOUNT, TIMESTAMP) VALUES (:account, :amount, :timestamp)")
                .build();
    }

    @Bean
    public Step partitionedMaster() {
        System.out.println("=================>partitionedMaster<===============================");
        return new StepBuilder("step1",jobRepository)
                .partitioner("step1", partitioner())
                .step(step1())
                .partitionHandler(partitionHandler())
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
    public MultiResourceItemReader<Transaction> multiResourceItemReader() {
        System.out.println("=======================>multiResourceItemReader<============================");
        return new MultiResourceItemReaderBuilder<Transaction>()
                .delegate(delegate())
                .name("multiresourceReader")
                .resources(inputResources)
                .build();
    }

    @Bean
    public FlatFileItemReader<Transaction> delegate() {
        System.out.println("==================>delegate<========================");
        return new FlatFileItemReaderBuilder<Transaction>()
                .name("flatFileTransactionReader")
                .delimited()
                .names(new String[] {"account", "amount", "timestamp"})
                .fieldSetMapper(fieldSet -> new Transaction(fieldSet.readString("account"),
                        fieldSet.readDate("timestamp", "yyyy-MM-dd HH:mm:ss"),
                        fieldSet.readBigDecimal("amount")))
                .build();
    }

    @Bean
    public Job parallelStepsJob() {
        return new JobBuilder("parallelStepsJob"+new Date(),jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(partitionedMaster())
                .build();
    }


}
