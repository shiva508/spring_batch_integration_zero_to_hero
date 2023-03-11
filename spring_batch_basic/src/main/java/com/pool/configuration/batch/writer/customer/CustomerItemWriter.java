package com.pool.configuration.batch.writer.customer;

import java.util.List;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pool.domin.Customer;
import com.pool.repository.CustomerRepository;

@Component
public class CustomerItemWriter implements ItemWriter<Customer> {
	
	@Autowired
	private CustomerRepository customerRepository;
	
	@Override
	public void write(List<? extends Customer> items) throws Exception {
		customerRepository.saveAll(items);
	}
}
