package com.pool.config;

import com.pool.records.GameByYear;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.PlatformTransactionManager;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Configuration
public class VideoGameCsvToDbStepConfig {

    private final DataSource dataSource;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final Resource resource;
    private final JdbcTemplate jdbcTemplate;

    public static final String EMPTY_CSV_STATUS = "EMPTY";
    public VideoGameCsvToDbStepConfig(DataSource dataSource,
                                      JobRepository jobRepository,
                                      PlatformTransactionManager transactionManager,
                                      @Value("${csv.file.path}") Resource resource,
                                      JdbcTemplate jdbcTemplate) {
        this.dataSource = dataSource;
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.resource = resource;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Bean
    public FlatFileItemReader<GameByYear> gameByYearReader(){
        return new FlatFileItemReaderBuilder<GameByYear>()
                .resource(resource)
                .name("csvVideoGameInput")
                .delimited().delimiter(",")
                .names("rank,name,platform,year,genre,publisher,na,eu,jp,other,global".split(","))
                .linesToSkip(1)
                .fieldSetMapper(fieldSet -> new GameByYear(
                        fieldSet.readInt("rank"),
                        fieldSet.readString("name"),
                        fieldSet.readString("platform"),
                        textToIntConverter(fieldSet.readString("year")),
                        fieldSet.readString("genre"),
                        fieldSet.readString("publisher"),
                        fieldSet.readFloat("na"),
                        fieldSet.readFloat("eu"),
                        fieldSet.readFloat("jp"),
                        fieldSet.readFloat("other"),
                        fieldSet.readFloat("global"))
                )
                .build();
    }


    @Bean
    public JdbcBatchItemWriter <GameByYear> gameByYearWriter(){
        String sql= """
                INSERT INTO video_game_sales(
                                                rank,
                                                name,
                                                platform,
                                                year,
                                                genre,
                                                publisher,
                                                na_sales,
                                                eu_sales,
                                                jp_sales,
                                                other_sales,
                                                global_sales)
                                                                VALUES (
                                                                :rank,
                                                                :name,
                                                                :platform,
                                                                :year,
                                                                :genre,
                                                                :publisher,
                                                                :na_sales,
                                                                :eu_sales,
                                                                :jp_sales,
                                                                :other_sales,
                                                                :global_sales)
                                                                ON CONFLICT ON CONSTRAINT video_game_sales_name_platform_year_genre_key do update set
                                                                rank=excluded.rank,
                                                                publisher=excluded.publisher,
                                                                na_sales=excluded.na_sales,
                                                                eu_sales=excluded.eu_sales,
                                                                jp_sales=excluded.jp_sales,
                                                                other_sales=excluded.other_sales,
                                                                global_sales=excluded.global_sales;
                                                                                                             
                """;
        return new JdbcBatchItemWriterBuilder<GameByYear>()
                .sql(sql)
                .dataSource(dataSource)
                .itemSqlParameterSourceProvider(csvRow -> {
                    var map=new HashMap<String,Object>();
                    map.putAll(Map.of(
                            "rank", csvRow.rank(),
                            "name",csvRow.name().trim(),
                            "platform",csvRow.platform().trim(),
                            "year",csvRow.year(),
                            "genre",csvRow.genre().trim(),
                            "publisher",csvRow.publisher().trim(),
                            "na_sales",csvRow.na(),
                            "eu_sales",csvRow.eu()));
                    map.putAll(Map.of(
                            "jp_sales",csvRow.jp(),
                            "other_sales",csvRow.other(),
                            "global_sales" ,csvRow.global()));
                    return new MapSqlParameterSource(map);
                }).build();

    }

    @Bean(name = "videoGameStep")
    public Step videoGameStep() throws IOException {
        return new StepBuilder("videoGameStep",jobRepository)
                .<GameByYear, GameByYear>chunk(100,transactionManager)
                .reader(gameByYearReader())
                .writer(gameByYearWriter())
                .listener(new StepExecutionListener() {
                    @Override
                    public ExitStatus afterStep(StepExecution stepExecution) {
                        var count= Objects.requireNonNull(
                                jdbcTemplate.queryForObject("select coalesce(count(*) ,0) from video_game_sales",Integer.class)
                        );
                        return count==0 ? new  ExitStatus(EMPTY_CSV_STATUS):ExitStatus.COMPLETED;
                    }
                })
                .build();
    }

    public int textToIntConverter(String text){
        if(null !=text && !text.contains("N/A") && !text.contains("NA")){
            return  Integer.parseInt(text);
        }else{
            return 0;
        }
    }
}
