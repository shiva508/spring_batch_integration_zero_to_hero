package com.pool.configuration.batch;// package com.pool.configuration.batch;

// import javax.persistence.EntityManagerFactory;

// import org.springframework.batch.core.Job;
// import org.springframework.batch.core.Step;
// import
// org.springframework.batch.core.configuration.annotation.AbstractBatchConfiguration;
// import
// org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
// import
// org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
// import
// org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
// import
// org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
// import org.springframework.batch.core.configuration.annotation.StepScope;
// import org.springframework.batch.core.launch.support.RunIdIncrementer;
// import org.springframework.batch.core.repository.JobRepository;
// import
// org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean;
// import org.springframework.batch.item.database.JpaItemWriter;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.stereotype.Component;
// import org.springframework.stereotype.Service;

// import com.pool.configuration.batch.reader.WeWishItemReader;
// import com.pool.domin.WeWish;

// @Configuration
// @EnableBatchProcessing
// public class WeWishBatchConfiguration extends DefaultBatchConfigurer {

// @Autowired
// public StepBuilderFactory stepBuilderFactory;

// @Autowired
// public JobBuilderFactory jobBuilderFactory;

// private WeWishItemReader<WeWish> reader;

// @Autowired
// private EntityManagerFactory emf;

// public void setReader(WeWishItemReader<WeWish> reader) {
// this.reader = reader;
// }

// @StepScope
// @Bean
// public WeWishItemReader<WeWish> reader() {
// return reader;
// }

// @StepScope
// @Bean
// public JpaItemWriter<WeWish> writer() {
// JpaItemWriter<WeWish> writer = new JpaItemWriter<>();
// writer.setEntityManagerFactory(emf);
// return writer;
// }

// @Bean
// public Job weWishJob() {
// return jobBuilderFactory
// .get("nameOfFactory")
// .incrementer(new RunIdIncrementer())
// .start(step1())
// .build();
// }

// @Bean
// public Step step1() {
// return stepBuilderFactory
// .get("stepName")
// .<WeWish, WeWish>chunk(10)
// .reader(reader())
// .writer(writer())
// .build();
// }

// @Override
// public JobRepository createJobRepository() throws Exception {
// MapJobRepositoryFactoryBean factoryBean = new MapJobRepositoryFactoryBean();
// factoryBean.afterPropertiesSet();
// return factoryBean.getObject();
// }
// }
