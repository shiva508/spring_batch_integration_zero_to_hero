package com.pool.configuration.batch;

import java.util.Iterator;
import java.util.List;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;
import com.pool.configuration.batch.reader.LaptoptemReader;
import com.pool.domin.Laptop;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
public class LaptopBatchConfiguration {

    @Autowired
    private LaptoptemReader<Laptop> laptoptemReader;

    @Autowired
    private EntityManagerFactory emf;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @StepScope
    @Bean
    public LaptoptemReader<Laptop> getLaptoptemReader() {
        return laptoptemReader;
    }

    public void setLaptoptemReader(LaptoptemReader<Laptop> laptoptemReader) {
        this.laptoptemReader = laptoptemReader;
    }

    @StepScope
    @Bean
    public JpaItemWriter<Laptop> laptopJpaItemWriter() {
        JpaItemWriter<Laptop> itemWriter = new JpaItemWriter<>();
        itemWriter.setEntityManagerFactory(emf);
        return itemWriter;
    }

    @Bean
    public Job laptopJob() {
        return new JobBuilder("laptopJob",jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(laptopStep())
                .build();
    }

    @Bean
    public Step laptopStep() {
        return new StepBuilder("laptopStep",jobRepository)
                .<Laptop, Laptop>chunk(100,transactionManager)
                .reader(getLaptoptemReader()).writer(laptopJpaItemWriter()).build();
    }

    /*@Bean
    public ItemReader<Laptop> laptopItemReader(@Value("#{jobParameters['laptops']}") List<Laptop> laptops) {
        Iterator<Laptop> iterator = laptops.iterator();
        ItemReader<Laptop> itemReader = new ItemReader<>() {

            @Override
            @Nullable
            public Laptop read()
                    throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {

                return iterator.hasNext() ? iterator.next() : null;
            }

        };
        return itemReader;
    }
*/
}
