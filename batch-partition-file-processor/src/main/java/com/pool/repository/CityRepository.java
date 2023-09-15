package com.pool.repository;

import com.pool.domine.CityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CityRepository extends JpaRepository<CityEntity,Long> {

    @Query(nativeQuery = true,value = "select id from city")
    public List<Long>  getCityIdList();
}
