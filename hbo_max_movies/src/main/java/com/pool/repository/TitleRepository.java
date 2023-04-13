package com.pool.repository;

import com.pool.entity.TitleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TitleRepository extends JpaRepository<TitleEntity,Long> {
}
