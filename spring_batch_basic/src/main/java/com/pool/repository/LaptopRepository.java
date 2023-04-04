package com.pool.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pool.domin.Laptop;

public interface LaptopRepository extends JpaRepository<Laptop, Long> {

}
