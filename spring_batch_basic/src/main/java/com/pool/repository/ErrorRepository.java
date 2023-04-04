package com.pool.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.pool.domin.ErrorEntity;

public interface ErrorRepository extends JpaRepository<ErrorEntity, Long> {

}
