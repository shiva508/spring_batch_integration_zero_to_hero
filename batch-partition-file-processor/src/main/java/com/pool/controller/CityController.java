package com.pool.controller;

import com.pool.config.partition.CityRangePartitioner;
import com.pool.domine.CountryEntity;
import com.pool.model.PartitionModel;
import com.pool.service.CountryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CityController {

    @Autowired
    private CityRangePartitioner cityRangePartitioner;

    @Autowired
    private CountryService countryService;

    @GetMapping("/batch/{gridSize}")
    public List<PartitionModel> getPartitionModels(@PathVariable("gridSize") Integer gridSize){
        return cityRangePartitioner.getPartitionModels(gridSize);
    }

    @GetMapping("/countries")
    public List<CountryEntity>  findAllCountries(){
        return countryService.findAll();
    }
}
