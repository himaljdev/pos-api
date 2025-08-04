package com.billing.service.repository;

import com.billing.service.enums.Status;
import com.billing.service.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> , JpaSpecificationExecutor<Customer> {
    List<Customer> findAllByStatusNot(Status status);
    List<Customer> findAllByStatus(Status status);
}