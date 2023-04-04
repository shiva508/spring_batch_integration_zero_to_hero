package com.pool.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pool.domin.Customer;

public interface CustomerRepository extends JpaRepository<Customer,Long> {

}
