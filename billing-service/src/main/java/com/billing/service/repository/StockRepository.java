package com.billing.service.repository;

import com.billing.service.dto.response.StockResponseDTO;
import com.billing.service.enums.Status;
import com.billing.service.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long>, JpaSpecificationExecutor<Stock> {
    Optional<Stock> findByIdAndLocation_CodeAndStatus(Long id, String locationCode, Status status);


    @Query("SELECT s.id AS id, s.lablePrice AS lablePrice, s.itemCost AS itemCost, " +
            "s.retailPrice AS retailPrice, s.wholesalePrice AS wholesalePrice, " +
            "s.retailDiscount AS retailDiscount, s.wholesaleDiscount AS wholesaleDiscount, " +
            "s.qty AS qty, s.status AS status, s.statusDescription AS statusDescription " +
            "FROM Stock s WHERE s.location.code = :location")
    List<StockResponseDTO> getStockProjection(@Param("location") String location);



} 