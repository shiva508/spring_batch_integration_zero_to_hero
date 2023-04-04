package com.pool.config.customize.jobreository;

import lombok.AllArgsConstructor;
import org.springframework.batch.core.configuration.BatchConfigurationException;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.JobExplorerFactoryBean;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.support.DatabaseType;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Isolation;

import javax.sql.DataSource;
@AllArgsConstructor
//@Configuration
public class CustomizeBatchConfig extends DefaultBatchConfiguration {

    private final DataSource dataSource;

    private final PlatformTransactionManager platformTransactionManager;

   /* ==============================CREATING CUSTOM JobRepository================================*/
    @Override
    public JobRepository jobRepository() throws BatchConfigurationException {
        JobRepositoryFactoryBean jobRepositoryFactoryBean=new JobRepositoryFactoryBean();
        try {
            jobRepositoryFactoryBean.setDatabaseType(DatabaseType.POSTGRES.getProductName());
            jobRepositoryFactoryBean.setTablePrefix("DEAR_COMARED_");
            jobRepositoryFactoryBean.setIsolationLevelForCreateEnum(Isolation.REPEATABLE_READ);
            jobRepositoryFactoryBean.setDataSource(dataSource);
            jobRepositoryFactoryBean.afterPropertiesSet();
            return jobRepositoryFactoryBean.getObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected PlatformTransactionManager getTransactionManager() {
        return platformTransactionManager;
    }

    @Override
    public JobExplorer jobExplorer() throws BatchConfigurationException {
        JobExplorerFactoryBean jobExplorerFactoryBean=new JobExplorerFactoryBean();
        try {
            jobExplorerFactoryBean.setDataSource(this.dataSource);
            jobExplorerFactoryBean.setTablePrefix("DEAR_COMARED_");
            return jobExplorerFactoryBean.getObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
