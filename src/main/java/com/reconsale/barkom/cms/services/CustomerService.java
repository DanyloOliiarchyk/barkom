package com.reconsale.barkom.cms.services;

import com.reconsale.barkom.cms.models.Customer;
import com.reconsale.barkom.cms.repositories.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    private CustomerRepository customerRepository;

    @Autowired
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public String getCustomerPassword(String name){
        return customerRepository.findByName(name).getPassword();
    }

    public void saveUserIfNotExist (Customer customer){
        if (customerRepository.findByName(customer.getName()) == null){
            customerRepository.save(customer);
        }
    }
}
