package com.reconsale.barkom.cms.repositories;

import com.reconsale.barkom.cms.models.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Query(value = "select c from Customer c where c.name = :n")
    Customer findByName (String n);
}
