package com.pool.configuration.batch.processor;

import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import com.pool.domin.Customer;
import com.pool.modal.CustomerModel;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CustomerProcessor implements ItemProcessor<CustomerModel, Customer> {
    @Override
    @Nullable
    public Customer process(@NonNull CustomerModel customerModel) throws Exception {
        Customer  customer=new Customer();
        customer.setGender(customerModel.getGender());
        customer.setContactNo(customerModel.getContactNo());
        customer.setCountry(customerModel.getCountry());
        customer.setDob(customerModel.getDob());
        customer.setEmail(customerModel.getEmail());
        customer.setFirstName(customerModel.getFirstName());
        customer.setLastName(customerModel.getLastName());
        String age = customerModel.getAge();
        StringUtils.isNumeric(age);
       /* if (age > 18) {
            log.info("Major");
            customer.setCitizedType("Major");
        } else {
            log.info("Minor");
            customer.setCitizedType("Minor");
        }*/
    
        return customer;
    }

   
}
