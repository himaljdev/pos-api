package com.billing.service.repository;

import com.billing.service.model.InvoiceSequence;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceSequenceRepository extends JpaRepository<InvoiceSequence, Integer> {}