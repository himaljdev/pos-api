package com.returns.service.repository;
import com.returns.service.enums.Status;
import com.returns.service.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long>, JpaSpecificationExecutor<Stock> {
    Optional<Stock> findByIdAndLocation_CodeAndStatus(Long id, String locationCode, Status status);
} 