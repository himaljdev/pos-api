package com.billing.service.repository;

import com.billing.service.enums.Status;
import com.billing.service.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long>, JpaSpecificationExecutor<Stock> {
    Optional<Stock> findByIdAndLocation_CodeAndStatus(Long id, String locationCode, Status status);
    List<Stock> findAllByItem_DescriptionContainingIgnoreCaseAndLocation_Code(String ite, String location);
} 