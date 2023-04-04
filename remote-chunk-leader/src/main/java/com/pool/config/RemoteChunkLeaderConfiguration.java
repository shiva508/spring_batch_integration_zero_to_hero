package com.pool.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pool.leader.annotation.LeaderChunkStep;
import com.pool.leader.annotation.LeaderInboundChunkChannel;
import com.pool.leader.annotation.LeaderItemWriter;
import com.pool.leader.annotation.LeaderOutboundChunkChannel;
import com.pool.record.Customer;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.amqp.dsl.Amqp;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.messaging.MessageChannel;
import org.springframework.transaction.PlatformTransactionManager;
import java.util.List;

@EnableBatchProcessing
@Configuration
public class RemoteChunkLeaderConfiguration {
    private final ObjectMapper objectMapper;
    public RemoteChunkLeaderConfiguration(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }



    @Bean
    @LeaderChunkStep
    public TaskletStep step(JobRepository repository, PlatformTransactionManager transactionManager,
                            @LeaderItemWriter ItemWriter<String> itemWriter) {
        var listItemReader = new ListItemReader<>(
                List.of(new Customer("Dave"), new Customer("Michael"), new Customer("Mahmoud")));
        return new StepBuilder("step", repository)//
                .<Customer, String>chunk(100, transactionManager)//
                .reader(listItemReader)//
                .processor(this::jsonFor)//
                .writer(itemWriter)//
                .build();
    }

    @Bean
    public IntegrationFlow outboundIntegrationFlow(@LeaderOutboundChunkChannel MessageChannel out, AmqpTemplate amqpTemplate) {
        return IntegrationFlow //
                .from(out)//
                .handle(Amqp.outboundAdapter(amqpTemplate).routingKey("requests"))//
                .get();
    }

    @Bean
    public IntegrationFlow inboundIntegrationFlow(ConnectionFactory cf, @LeaderInboundChunkChannel MessageChannel in) {
        return IntegrationFlow//
                .from(Amqp.inboundAdapter(cf, "replies"))//
                .channel(in)//
                .get();
    }

    @Bean
    public Job job(JobRepository repository, Step step) {
        return new JobBuilder("job", repository)//
                .start(step)//
                .incrementer(new RunIdIncrementer())//
                .build();
    }


    private String jsonFor(Object o) {
        try {
            return objectMapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
