package com.pool.config.reader;

import com.pool.domine.CityEntity;
import com.pool.model.PartitionModel;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.orm.JpaNativeQueryProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

@Component
@Scope(value = "prototype",proxyMode = ScopedProxyMode.TARGET_CLASS)
public class CityItemReaderBuilder {

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    public static String QUERY= """
            select * from city where id between :start and :end
            """;
    public ItemReader<CityEntity> getCitiesByRange(PartitionModel partitionModel){
        JpaPagingItemReader<CityEntity> pagingItemReader=new JpaPagingItemReader<>();

        try {
            JpaNativeQueryProvider<CityEntity> queryProvider=new JpaNativeQueryProvider<>();
            queryProvider.setSqlQuery(QUERY);
            queryProvider.setEntityClass(CityEntity.class);
            queryProvider.afterPropertiesSet();

            Map<String,Object> stringObjectMap=new HashMap<>();
            stringObjectMap.put("start",partitionModel.getStart());
            stringObjectMap.put("end",partitionModel.getEnd());
            pagingItemReader.setParameterValues(stringObjectMap);
            pagingItemReader.setEntityManagerFactory(entityManagerFactory);
            pagingItemReader.setQueryProvider(queryProvider);
            pagingItemReader.afterPropertiesSet();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return pagingItemReader;
    }
}
