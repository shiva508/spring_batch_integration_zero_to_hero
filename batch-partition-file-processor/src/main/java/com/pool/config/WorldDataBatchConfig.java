package com.pool.config;

import com.pool.config.partition.CityRangePartitioner;
import com.pool.config.reader.CityItemReaderBuilder;
import com.pool.domine.CityEntity;
import com.pool.model.PartitionModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import java.util.List;

@Configuration
@Slf4j
public class WorldDataBatchConfig {

    @Autowired
    private CityRangePartitioner cityRangePartitioner;
    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private PlatformTransactionManager platformTransactionManager;
    @Autowired
    private CityItemReaderBuilder cityItemReaderBuilder;

    public Flow cityFlow(String cityStep,int gridSize) {
        Flow cityFlow = null;
        Flow[] flows = new Flow[gridSize];
        List<PartitionModel> partitionModels = cityRangePartitioner.getPartitionModels(gridSize);
        if (!partitionModels.isEmpty()) {
            for (int i = 0; i < partitionModels.size(); i++) {
                    flows[i] = new FlowBuilder<Flow>("City-flow" + i).start(cityStep(cityStep+"-->" + i, partitionModels.get(i))).build();
            }
            cityFlow=new FlowBuilder<Flow>("cityFlow").split(asyncTaskExecutor()).add(flows).build();
        }
        return cityFlow;
    }

    @Bean
    public TaskExecutor asyncTaskExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor=new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(2);
        threadPoolTaskExecutor.setMaxPoolSize(10);
        return threadPoolTaskExecutor;
    }

    public Step cityStep(String s, PartitionModel partitionModel) {
        log.info("hashcode={}",cityItemReaderBuilder);
        return new StepBuilder(s,jobRepository).<CityEntity,CityEntity>chunk(50,platformTransactionManager)
                .reader(cityItemReaderBuilder.getCitiesByRange(partitionModel))
                .writer(new ItemWriter<CityEntity>() {
                    @Override
                    public void write(Chunk<? extends CityEntity> chunk) throws Exception {
                        System.out.println();
                        chunk.getItems().forEach(cityEntity -> {
                            log.info("{}",cityEntity);
                        });
                    }
                })
                .build();
    }

    @Bean("cityJob")
    public Job cityJob(){
        Flow flow=this.cityFlow("city",100);
        return new JobBuilder("city",jobRepository).incrementer(new RunIdIncrementer()).start(flow).build().build();
    }
}
