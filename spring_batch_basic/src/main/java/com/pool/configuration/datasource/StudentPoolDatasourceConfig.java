package com.pool.configuration.datasource;


import javax.sql.DataSource;

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

//@Configuration
public class StudentPoolDatasourceConfig {
	/*@Primary
	@ConfigurationProperties(prefix = "spring.datasource")
	public DataSource datasource() {
		return DataSourceBuilder.create().build();
	}

	@Bean(name = "dataSourceShiva")
	@ConfigurationProperties(prefix = "spring.shivadatasource")
	public DataSource dataSourceShiva() {
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
	
	@Primary
	@Bean
	public JpaTransactionManager jpaTransactionManager() {
		 JpaTransactionManager jpaTransactionManager=new JpaTransactionManager();
		 jpaTransactionManager.setDataSource(datasource());
		 jpaTransactionManager.setEntityManagerFactory(datasourceOneEntityManagerFactory());
		 return jpaTransactionManager;
	}
	*/
}
