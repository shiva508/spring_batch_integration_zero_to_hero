package com.pool.service;

import com.pool.domine.CityEntity;
import com.pool.repository.CityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CityServiceImpl implements CityService{
    @Autowired
    private CityRepository cityRepository;
    @Override
    public List<CityEntity> getCities() {
        return cityRepository.findAll();
    }
}
