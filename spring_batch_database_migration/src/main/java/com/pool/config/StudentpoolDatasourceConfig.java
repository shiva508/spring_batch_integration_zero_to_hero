package com.pool.config;

import javax.sql.DataSource;

import jakarta.persistence.EntityManagerFactory;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

@Configuration
public class StudentpoolDatasourceConfig {
	@Primary
	@Bean
	@ConfigurationProperties(prefix = "spring.datasource")
	public DataSource datasource() {
		return DataSourceBuilder.create().build();
	}

	@Bean
	@ConfigurationProperties(prefix = "spring.datasourcetwo")
	public DataSource datasourcetwo() {
		return DataSourceBuilder.create().build();
	}
	
	@Bean
	@Primary
	public EntityManagerFactory datasourceOneEntityManagerFactory() {
		LocalContainerEntityManagerFactoryBean beanone=new LocalContainerEntityManagerFactoryBean();
		beanone.setDataSource(datasource());
		beanone.setPackagesToScan("com.pool.datasourceone.entity");
		beanone.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
		beanone.setPersistenceProviderClass(HibernatePersistenceProvider.class);
		beanone.afterPropertiesSet();
		return beanone.getObject();
	}
	
	@Bean
	public EntityManagerFactory datasourceTwoEntityManagerFactory() {
		LocalContainerEntityManagerFactoryBean beanone=new LocalContainerEntityManagerFactoryBean();
		beanone.setDataSource(datasourcetwo());
		beanone.setPackagesToScan("com.pool.datasourcetwo.entity");
		beanone.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
		beanone.setPersistenceProviderClass(HibernatePersistenceProvider.class);
		beanone.afterPropertiesSet();
		return beanone.getObject();
	}
	
	@Primary
	@Bean
	public JpaTransactionManager jpaTransactionManager() {
		 JpaTransactionManager jpaTransactionManager=new JpaTransactionManager();
		 jpaTransactionManager.setDataSource(datasource());
		 jpaTransactionManager.setEntityManagerFactory(datasourceOneEntityManagerFactory());
		 return jpaTransactionManager;
	}
	
	
}
