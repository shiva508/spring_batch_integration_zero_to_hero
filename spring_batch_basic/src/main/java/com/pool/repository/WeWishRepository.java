package com.pool.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.pool.domin.WeWish;

@Repository
public interface WeWishRepository extends JpaRepository<WeWish, Long> {

}
