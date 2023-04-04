package com.pool.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pool.records.YearPlatformSales;
import com.pool.records.YearReport;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.integration.chunk.ChunkMessageChannelItemWriter;
import org.springframework.batch.integration.chunk.RemoteChunkHandlerFactoryBean;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.boot.autoconfigure.batch.JobExecutionEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.integration.amqp.dsl.Amqp;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.messaging.MessageChannel;
import org.springframework.transaction.PlatformTransactionManager;
import javax.sql.DataSource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@EnableBatchProcessing
@Configuration
    public class YearReportStepConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DataSource dataSource;

    private final ObjectMapper objectMapper;
    public static final String EMPTY_CSV_STATUS = "EMPTY";
    private final Map<Integer,YearReport> integerYearReportMap=new ConcurrentHashMap<>();
    public YearReportStepConfiguration(JobRepository jobRepository,
                                       PlatformTransactionManager transactionManager,
                                       DataSource dataSource,
                                       ObjectMapper objectMapper) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.dataSource=dataSource;
        this.objectMapper= objectMapper;
    }

    @EventListener
    public void batchJobCompleted(JobExecutionEvent event) {
        var running = Map.of(//
                "running", event.getJobExecution().getStatus().isRunning(),//
                "finished", event.getJobExecution().getExitStatus().getExitCode() //
        );//
        System.out.println("jobExecutionEvent: [" + running + "]");
        this.integerYearReportMap.clear();
    }

    private final RowMapper<YearReport>  yearReportRowMapper= (rs, rowNum) -> {
        Integer year=rs.getInt("year");
        if(!this.integerYearReportMap.containsKey(year)){
            this.integerYearReportMap.put(year,new YearReport(year,new ArrayList<>()));
        }
        YearReport yearReport = integerYearReportMap.get(year);
        yearReport.breakout().add(new YearPlatformSales(rs.getInt("year"), rs.getString("platform"), rs.getFloat("sales")));
        return yearReport;
    };

    @Bean
    public ItemReader<YearReport> yearReportItemReader() {
        String query=""" 
                SELECT year,
                	   ypr.platform,
                	   ypr.sales,
                	   (select count(yps.year) from year_platform_report yps where yps.year=ypr.year) as count
                	   FROM year_platform_report ypr where ypr.year>0 order by year
                     """;
       return new JdbcCursorItemReaderBuilder<YearReport>()
                        .name("yearReportItemReader")
                        .sql(query)
                        .dataSource(this.dataSource)
                        .rowMapper(this.yearReportRowMapper)
                        .build();
    }

    @Bean
    public TaskletStep yearReportStep(){
        return new StepBuilder("yearReportStep",jobRepository)
                .<YearReport, String>chunk(1000,transactionManager)
                .reader(yearReportItemReader())
                .processor(objectMapper::writeValueAsString)
                .writer(chunkMessageChannelItemWriter()/*chunk -> {
                    var deDupped = new LinkedHashSet<>(chunk.getItems());
                   // System.out.println(deDupped);
                }*/).build();
    }

    @Bean
    public MessageChannel requests() {
        return  MessageChannels.direct().get();
    }

    @Bean
    public QueueChannel replies() {
        return  new QueueChannel();
    }
    @Bean
    public IntegrationFlow outboundFlow(AmqpTemplate amqpTemplate) {
        return IntegrationFlow.from(requests())
                .handle(Amqp.outboundAdapter(amqpTemplate)
                        .routingKey("requests"))
                .get();
    }

    @Bean
    public MessagingTemplate messagingTemplate() {
        MessagingTemplate template = new MessagingTemplate();
        template.setDefaultChannel(requests());
        template.setReceiveTimeout(2000);
        return template;
    }

    public static class DedupingChunkMessageChannelItemWriter<T> extends ChunkMessageChannelItemWriter<T>{
        @Override
        public void write(Chunk<? extends T> items) throws Exception {
            System.out.println("Called from here");
           var inputCollection=items.getItems();
            var newList=new ArrayList<>(new LinkedHashSet<>(inputCollection));
            super.write(new Chunk<>(newList));
        }
    }

    @Bean
    @StepScope
    public ChunkMessageChannelItemWriter<String> chunkMessageChannelItemWriter() {
        ChunkMessageChannelItemWriter<String> chunkMessageChannelItemWriter = new DedupingChunkMessageChannelItemWriter<String>();
        chunkMessageChannelItemWriter.setMessagingOperations(messagingTemplate());
        chunkMessageChannelItemWriter.setReplyChannel(replies());
        return chunkMessageChannelItemWriter;
    }

    @Bean
    public RemoteChunkHandlerFactoryBean<String> chunkHandler() throws Exception {
        ChunkMessageChannelItemWriter<String> proxyObject=(chunkMessageChannelItemWriter());
        RemoteChunkHandlerFactoryBean<String> remoteChunkHandlerFactoryBean = new RemoteChunkHandlerFactoryBean<String>();
        //remoteChunkHandlerFactoryBean.setChunkWriter(chunkMessageChannelItemWriter());
        remoteChunkHandlerFactoryBean.setChunkWriter(proxyObject);
        remoteChunkHandlerFactoryBean.setStep(yearReportStep());
        return remoteChunkHandlerFactoryBean;
    }

    @Bean
    public IntegrationFlow replyFlow(ConnectionFactory connectionFactory) {
        return IntegrationFlow
                .from(Amqp.inboundAdapter(connectionFactory, "replies"))
                .channel(replies())
                .get();
    }


}
