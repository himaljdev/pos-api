package com.returns.service.repository;

import com.returns.service.model.ReturnsInvoiceSequence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReturnsInvoiceSequenceRepository extends JpaRepository<ReturnsInvoiceSequence, Integer> {
}
