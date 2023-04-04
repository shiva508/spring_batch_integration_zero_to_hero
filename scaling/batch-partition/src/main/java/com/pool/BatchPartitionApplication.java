package com.pool;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class BatchPartitionApplication {
    public static void main(String[] args) {

        String [] newArgs = new String[] {"inputFiles=/data/csv/transactions*.csv"};

        List<String> strings = Arrays.asList(args);

        List<String> finalArgs = new ArrayList<>(strings.size() + 1);
        finalArgs.addAll(strings);
        finalArgs.add("inputFiles=/data/csv/transactions*.csv");

        SpringApplication.run(BatchPartitionApplication.class,finalArgs.toArray(new String[finalArgs.size()]));
    }
}