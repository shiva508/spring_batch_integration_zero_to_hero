package com.pool.config;

import com.pool.config.listener.TitleBadRecordListener;
import com.pool.config.writer.HboCreditWriter;
import com.pool.config.writer.HboTitleWriter;
import com.pool.entity.CreditEntity;
import com.pool.entity.TitleEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.skip.AlwaysSkipItemSkipPolicy;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.transaction.PlatformTransactionManager;

@EnableBatchProcessing
@Configuration
@Slf4j
@AllArgsConstructor
public class HboMaxMoviesBatchConfig {

    private final JobRepository jobRepository;

    private final PlatformTransactionManager platformTransactionManager;

    private final TitleBadRecordListener titleBadRecordListener;

    private final HboTitleWriter hboTitleWriter;

    private final HboCreditWriter hboCreditWriter;

    @Bean("hboTitleJob")
    public Job hboTitleJob(){
        return new JobBuilder("hboTitleJob",jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(hboTitleStep())
                .next(hboCreditStep())
                .build();
    }
    @Bean("hboTitleStep")
    public Step hboTitleStep(){
        return new StepBuilder("hboTitleStep",jobRepository)
                .<TitleEntity,TitleEntity>chunk(100,platformTransactionManager)
                .reader(hboTitleReader(null))
                //.writer(chunk ->System.out.println(chunk.getItems().size()))
                .writer(hboTitleWriter)
                .faultTolerant()
                .skip(Throwable.class)
                .skip(FlatFileParseException.class)
                .skipLimit(Integer.MAX_VALUE)
                .skipPolicy(new AlwaysSkipItemSkipPolicy())
                .listener(titleBadRecordListener)
                .build();
    }


    @Bean("hboTitleReader")
    @StepScope
    public FlatFileItemReader<TitleEntity> hboTitleReader(@Value("${hbo.title.file.path}")Resource resource){
        System.out.println(resource.getFilename());
        return new FlatFileItemReaderBuilder<TitleEntity>()
                .resource(resource)
                .name("hboTitleReader")
                .linesToSkip(1)
                .delimited().delimiter(",")
                .names(new String[]{"id",
                                    "title",
                                    "type",
                                    "description",
                                    "release_year",
                                    "age_certification",
                                    "runtime",
                                    "genres",
                                    "production_countries",
                                    "seasons",
                                    "imdb_id",
                                    "imdb_score",
                                    "imdb_votes",
                                    "tmdb_popularity",
                                    "tmdb_score"})
                .fieldSetMapper(fieldSet -> TitleEntity.builder()
                        .movieId(fieldSet.readString("id").trim())
                        .title(fieldSet.readString("title").trim())
                        .type(fieldSet.readString("type").trim())
                        //.description(fieldSet.readString("description").trim())
                        .releaseYear(fieldSet.readString("release_year").trim())
                        .ageCertification(fieldSet.readString("age_certification").trim())
                        .runTime(fieldSet.readString("runtime").trim())
                        .genres(fieldSet.readString("genres").trim())
                        .productionCountries(fieldSet.readString("production_countries").trim())
                        .seasons(fieldSet.readString("seasons").trim())
                        .imdbId(fieldSet.readString("imdb_id").trim())
                        .imdbScore(fieldSet.readString("imdb_score").trim())
                        .imdbVotes(fieldSet.readString("imdb_votes").trim())
                        .tmdbPopularity(fieldSet.readString("tmdb_popularity").trim())
                        .tmdbScore(fieldSet.readString("tmdb_score").trim())
                        .build())
                .build();
    }


    @Bean("hboCreditStep")
    public Step hboCreditStep(){
        return new StepBuilder("hboCreditStep",jobRepository)
                .<CreditEntity,CreditEntity>chunk(1000,platformTransactionManager)
                .reader(hboCreditReader(null))
                .writer(hboCreditWriter)
                .faultTolerant()
                .skip(Throwable.class)
                .skip(FlatFileParseException.class)
                .skipLimit(Integer.MAX_VALUE)
                .skipPolicy(new AlwaysSkipItemSkipPolicy())
                .build();
    }

    @Bean("hboCreditReader")
    @StepScope
    public FlatFileItemReader<CreditEntity> hboCreditReader(@Value("${hbo.credit.file.path}") Resource resource){
        return new FlatFileItemReaderBuilder<CreditEntity>()
                .name("hboCreditReader")
                .linesToSkip(1)
                .resource(resource)
                .delimited().delimiter(",")
                .names(new String[]{"person_id",
                                    "id",
                                    "name",
                                    "character",
                                    "role"})
                .fieldSetMapper(fieldSet -> CreditEntity.builder()
                        .personId(fieldSet.readString("person_id"))
                        .movieId(fieldSet.readString("id"))
                        .name(fieldSet.readString("name"))
                        .character(fieldSet.readString("character"))
                        .role(fieldSet.readString("role"))
                        .build())
                .build();
    }
}
