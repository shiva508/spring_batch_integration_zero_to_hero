package com.pool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.Date;

@SpringBootApplication
@Slf4j
public class TdgtsbBookApplication implements CommandLineRunner {

    private final JobLauncher jobLauncher;
    private final Job basicJob;
    private final Job taskletJob;
    private final Job taskletLateBindingJob;
    private final Job jobValidatorJob;
    private final Job runIdIncrementerJob;
    private final Job listenerJob;
    private final Job executionContextJob;
    private final Job  callableJob;
    private final Job methodInvokingJob;
    private final Job methodInvokingParamJob;
    private final Job systemCommandJob;
    private final Job chunkJob;
    private final Job chunkSizeJob;
    private final Job chunkCompletionJob;
    private final Job chunkRandomCompletionJob;
    private final Job chunkListenerJob;
    private final Job examinationjob;
    private final Job examinationDeciderjob;
    private final Job examinationEndJob;
    private final Job examinationFailJob;
    private final Job examinationStopRestartlJob;
    private final Job iplScheduleSummaryJob;
    private final Job iplScheduleSummaryJobFlowStep;
    private final Job iplScheduleSummaryJobJob;
    private final Job jobMetadataJob;
    public TdgtsbBookApplication(JobLauncher jobLauncher,
                                 @Qualifier("basicJob") Job basicJob,
                                 @Qualifier("taskletJob") Job taskletJob,
                                 @Qualifier("taskletLateBindingJob") Job taskletLateBindingJob,
                                 @Qualifier("jobValidatorJob") Job jobValidatorJob,
                                 @Qualifier("runIdIncrementerJob") Job runIdIncrementerJob,
                                 @Qualifier("listenerJob") Job listenerJob,
                                 @Qualifier("executionContextJob") Job executionContextJob,
                                 @Qualifier("callableJob") Job  callableJob,
                                 @Qualifier("methodInvokingJob") Job methodInvokingJob,
                                 @Qualifier("methodInvokingParamJob") Job methodInvokingParamJob,
                                 @Qualifier("systemCommandJob") Job systemCommandJob,
                                 @Qualifier("chunkJob") Job chunkJob,
                                 @Qualifier("chunkSizeJob") Job chunkSizeJob,
                                 @Qualifier("chunkCompletionJob") Job chunkCompletionJob,
                                 @Qualifier("chunkRandomCompletionJob") Job chunkRandomCompletionJob,
                                 @Qualifier("chunkListenerJob") Job chunkListenerJob,
                                 @Qualifier("examinationjob") Job examinationjob,
                                 @Qualifier("examinationDeciderjob") Job examinationDeciderjob,
                                 @Qualifier("examinationEndJob") Job examinationEndJob,
                                 @Qualifier("examinationFailJob") Job examinationFailJob,
                                 @Qualifier("examinationStopRestartlJob") Job examinationStopRestartlJob,
                                 @Qualifier("iplScheduleSummaryJob") Job iplScheduleSummaryJob,
                                 @Qualifier("iplScheduleSummaryJobFlowStep") Job iplScheduleSummaryJobFlowStep,
                                 @Qualifier("iplScheduleSummaryJobJob") Job iplScheduleSummaryJobJob,
                                 @Qualifier("jobMetadataJob") Job jobMetadataJob
                                 ) {
        this.jobLauncher = jobLauncher;
        this.basicJob = basicJob;
        this.taskletJob = taskletJob;
        this.taskletLateBindingJob=taskletLateBindingJob;
        this.jobValidatorJob=jobValidatorJob;
        this.runIdIncrementerJob=runIdIncrementerJob;
        this.listenerJob=listenerJob;
        this.executionContextJob=executionContextJob;
        this.callableJob=callableJob;
        this.methodInvokingJob=methodInvokingJob;
        this.methodInvokingParamJob=methodInvokingParamJob;
        this.systemCommandJob=systemCommandJob;
        this.chunkJob=chunkJob;
        this.chunkSizeJob=chunkSizeJob;
        this.chunkCompletionJob=chunkCompletionJob;
        this.chunkRandomCompletionJob=chunkRandomCompletionJob;
        this.chunkListenerJob=chunkListenerJob;
        this.examinationjob=examinationjob;
        this.examinationDeciderjob=examinationDeciderjob;
        this.examinationEndJob=examinationEndJob;
        this.examinationFailJob=examinationFailJob;
        this.examinationStopRestartlJob=examinationStopRestartlJob;
        this.iplScheduleSummaryJob=iplScheduleSummaryJob;
        this.iplScheduleSummaryJobFlowStep=iplScheduleSummaryJobFlowStep;
        this.iplScheduleSummaryJobJob=iplScheduleSummaryJobJob;
        this.jobMetadataJob=jobMetadataJob;
    }

    public static void main(String[] args) {
        SpringApplication.run(TdgtsbBookApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Job Triggered");
                jobLauncher.run(jobMetadataJob,new JobParametersBuilder()
                .addDate("iplScheduleSummaryJob",new Date())
                .addString("username","dasari508")
                .addString("csvFilePath","/home/shiva/shiva/mywork/spring_batch_zero_to_hero/src/main/resources/output/students.csv")
                .addString("inputFile","file://${HOME}/shiva/mywork/assignment/dada/spring_batch_integration_zero_to_hero/spring_batch_basic/src/main/resources/students.csv")
                .addString("outputFile","file://${HOME}/shiva/mywork/assignment/dada/spring_batch_integration_zero_to_hero/spring_batch_basic/src/main/resources/myoutp.csv")
                .toJobParameters());
    }

}