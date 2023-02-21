package com.pool.config;

import com.pool.records.YearPlatformSales;
import com.pool.records.YearReport;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.boot.autoconfigure.batch.JobExecutionEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.RowMapper;
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
    public static final String EMPTY_CSV_STATUS = "EMPTY";
    private final Map<Integer,YearReport> integerYearReportMap=new ConcurrentHashMap<>();
    public YearReportStepConfiguration(JobRepository jobRepository,
                                       PlatformTransactionManager transactionManager,
                                       DataSource dataSource) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.dataSource=dataSource;
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
    public Step yearReportStep(){
        return new StepBuilder("yearReportStep",jobRepository)
                .<YearReport, YearReport>chunk(1000,transactionManager)
                .reader(yearReportItemReader())
                .writer(chunk -> {
                    var deDupped = new LinkedHashSet<>(chunk.getItems());
                   // System.out.println(deDupped);
                }).build();
    }

}
