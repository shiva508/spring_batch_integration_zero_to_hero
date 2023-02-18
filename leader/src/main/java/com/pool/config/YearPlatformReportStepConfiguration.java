package com.pool.config;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Configuration
public class YearPlatformReportStepConfiguration {
private final JobRepository jobRepository;
private final JdbcTemplate jdbcTemplate;
private final PlatformTransactionManager transactionManager;
private final TransactionTemplate transactionTemplate;

    public YearPlatformReportStepConfiguration(JobRepository jobRepository,
                                               JdbcTemplate jdbcTemplate,
                                               PlatformTransactionManager transactionManager,
                                               TransactionTemplate transactionTemplate) {
        this.jobRepository = jobRepository;
        this.jdbcTemplate = jdbcTemplate;
        this.transactionManager = transactionManager;
        this.transactionTemplate = transactionTemplate;
    }

    @Bean
    public Step yearPlatformReportStep(){

        return new StepBuilder("yearPlatformReportStep",jobRepository)
                .tasklet((contribution, chunkContext) ->//
                        transactionTemplate.execute(status -> {
                            jdbcTemplate.execute(
                                    """
                                                insert into year_platform_report (year, platform)
                                                select year, platform from video_game_sales
                                                on conflict on constraint year_platform_report_year_platform_key do nothing;
                                            """);
                            jdbcTemplate.execute("""
                                    insert into year_platform_report (year, platform, sales)
                                    select yp1.year,
                                    yp1.platform, (
                                                select sum(vgs.global_sales) from video_game_sales vgs
                                                where vgs.platform = yp1.platform and vgs.year = yp1.year
                                            )
                                    from year_platform_report as yp1
                                    on conflict on constraint year_platform_report_year_platform_key
                                    do update set
                                    year = excluded.year,
                                    platform = excluded.platform,
                                    sales = excluded.sales;
                                    """);
                            System.out.println("Data Processing");
                            return RepeatStatus.FINISHED;
                        }), transactionManager)//
                .build();
    }
}
