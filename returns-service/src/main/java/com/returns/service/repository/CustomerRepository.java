package com.returns.service.repository;
import com.returns.service.enums.Status;
import com.returns.service.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    List<Customer> findAllByStatusNot(Status status);
}