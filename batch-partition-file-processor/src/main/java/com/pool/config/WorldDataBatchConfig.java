package com.pool.config;

import com.pool.config.partition.CityRangePartitioner;
import com.pool.config.reader.CityItemReaderBuilder;
import com.pool.config.writer.WorldExcelWriterBuilder;
import com.pool.domine.CityEntity;
import com.pool.model.PartitionModel;
import com.pool.util.ExcelUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.listener.ExecutionContextPromotionListener;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
    @Autowired
    private WorldExcelWriterBuilder worldExcelWriterBuilder;

    @Autowired
    private ExcelUtil excelUtil;

    public Flow cityFlow(String cityStep,int gridSize) {
        Flow cityFlow = null;
        Flow[] flows = new Flow[gridSize];
        List<PartitionModel> partitionModels = cityRangePartitioner.getPartitionModels(gridSize);
        if (!partitionModels.isEmpty()) {
            for (int i = 0; i < partitionModels.size(); i++) {
                    flows[i] = new FlowBuilder<Flow>("City-flow" + i).start(cityStep(i, partitionModels.get(i))).build();
            }
            cityFlow=new FlowBuilder<Flow>("cityFlow").split(asyncTaskExecutor()).add(flows).build();
        }
        return cityFlow;
    }

    @Bean
    public TaskExecutor asyncTaskExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor=new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(8);
        threadPoolTaskExecutor.setMaxPoolSize(10);
        return threadPoolTaskExecutor;
    }

    public Step cityStep(int flowName, PartitionModel partitionModel) {
        log.info("hashcode={}",cityItemReaderBuilder);
        return new StepBuilder("cityStep->" + flowName,jobRepository)
                .<CityEntity,CityEntity>chunk(50, platformTransactionManager)
                .listener(new StepExecutionListener() {
                    @Override
                    public void beforeStep(StepExecution stepExecution) {
                        stepExecution.getExecutionContext().putString("maxrow",String.valueOf(flowName));
                    }
                    @Override
                    public ExitStatus afterStep(StepExecution stepExecution) {
                        return StepExecutionListener.super.afterStep(stepExecution);
                    }
                })
                .reader(cityItemReaderBuilder.getCitiesByRange(partitionModel))
                .writer(worldExcelWriterBuilder)
                .build();
    }

    @Bean("cityJob")
    public Job cityJob(){
        List<PartitionModel> partitionModels = cityRangePartitioner.getPartitionModels(100);
        Flow dataFlow=this.cityFlow("city",100);
        return new JobBuilder("city",jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(excelSheetFlow(partitionModels.size()))
                .next(dataFlow)
                .build()
                .build();
    }

    public Flow excelSheetFlow(int partitionsSize){
        return new FlowBuilder<Flow>("excelSheetFlow").start(excelSheetStep(partitionsSize)).build();
    }
    @Bean("excelSheetStep")
    public Step excelSheetStep(int partitionsSize){
        return new StepBuilder("excelSheetStep",jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    String fileName= excelUtil.createExcelSheet(partitionsSize);
                    //chunkContext.getStepContext().getJobExecutionContext().put("fileName",fileName);
                    chunkContext.getStepContext().getStepExecution().getExecutionContext().put("fileName",fileName);
                    contribution.getStepExecution().getExecutionContext().put("fileName",fileName);
                    return RepeatStatus.FINISHED;
        },platformTransactionManager)
                .listener(metaDataExecutionContextPromotionListener())
                .build();
    }

    @Bean("metaDataExecutionContextPromotionListener")
    public ExecutionContextPromotionListener metaDataExecutionContextPromotionListener(){
        ExecutionContextPromotionListener promotionListener=new ExecutionContextPromotionListener();
        promotionListener.setKeys(new String[]{"fileName"});
        // promotionListener.setStrict(true);
        return promotionListener;
    }
}
