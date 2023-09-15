package com.pool.service;

import com.pool.domine.CountryEntity;
import com.pool.repository.CountryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Scope(value = "prototype",proxyMode = ScopedProxyMode.TARGET_CLASS)
public class CountryServiceImpl implements CountryService{

    @Autowired
    private CountryRepository countryRepository;

    @Override
    public List<CountryEntity> findAll(){
        return countryRepository.findAll();
    }
}
