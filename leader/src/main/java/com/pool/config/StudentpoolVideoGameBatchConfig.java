package com.pool.config;

import com.pool.records.GameByYear;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

//@Configuration
//@EnableBatchProcessing
public class StudentpoolVideoGameBatchConfig {

    public int textToIntConerter(String text){
    if(null !=text && !text.contains("N/A") && !text.contains("NA")){
        return  Integer.parseInt(text);
    }else{
        return 0;
        }
    }
    //@Bean
    public FlatFileItemReader<GameByYear> csvVideoGameFlatFileItemReader(@Value("file://${HOME}/shiva/mywork/assignment/spring_batch_zero_to_hero/data/vgsales.csv") Resource resource){

        return new FlatFileItemReaderBuilder<GameByYear>()
                .resource(resource)
                .name("csvVideoGameInput")
                .delimited().delimiter(",")//
                .names("rank,name,platform,year,genre,publisher,na,eu,jp,other,global".split(","))
                .linesToSkip(1)
                .fieldSetMapper(fieldSet -> new GameByYear(
                        fieldSet.readInt("rank"),
                        fieldSet.readString("name"),
                        fieldSet.readString("platform"),
                        textToIntConerter(fieldSet.readString("year")),
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
    //@Bean(name = "videoGameStep")
    public Step videoGameStep(JobRepository jobRepository,
                              PlatformTransactionManager transactionManager,
                              FlatFileItemReader<GameByYear> csvVideoGameFlatFileItemReader,
                              JdbcBatchItemWriter <GameByYear> csvJdbcBatchItemWriter
                              ) throws IOException {
        /*var lines=(String[])null;
        try(var reader=new InputStreamReader(resource.getInputStream())){
            var string= FileCopyUtils.copyToString(reader);
            lines= string.split(System.lineSeparator());
            System.out.println("There are "+lines.length+" lines ");
        }*/

        return new StepBuilder("videoGameStep",jobRepository)
                .<GameByYear, GameByYear>chunk(100,transactionManager)
                .reader(csvVideoGameFlatFileItemReader)
                .writer(csvJdbcBatchItemWriter).build();
    }

    //@Bean
    public JdbcBatchItemWriter <GameByYear> csvJdbcBatchItemWriter(DataSource dataSource){
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
                })
                /*.itemPreparedStatementSetter((csvRow,  ps) ->{
                                            var i=0;
                                            ps.setInt(i++,    csvRow.rank() );
                                            ps.setString(i++, csvRow.name());
                                            ps.setString(i++, csvRow.platform());
                                            ps.setInt(i++,    csvRow.year());
                                            ps.setString(i++, csvRow.genre());
                                            ps.setString(i++, csvRow.publisher());
                                            ps.setFloat(i++,  csvRow.na());
                                            ps.setFloat(i++,  csvRow.eu());
                                            ps.setFloat(i++,  csvRow.jp());
                                            ps.setFloat(i++,  csvRow.other());
                                            ps.setFloat(i++,  csvRow.global());
                                            ps.execute();
                })*/.build();

    }

    //@Bean(name = "videoGameJob")
    public Job videoGameJob(JobRepository jobRepository,
                            @Qualifier("videoGameStep") Step videoGameStep,
                            EndStepConfiguration endStepConfiguration){
        return new JobBuilder("videoGameJob",jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(videoGameStep)
                .build();
    }
}
