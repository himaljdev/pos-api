package com.billing.service.repository;

import com.billing.service.model.CashInOut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface CashInOutRepository extends JpaRepository<CashInOut, Long> {
    List<CashInOut> findAllByCashierUser_UsernameAndCreatedDateBetween(
            String username,
            Date startDate,
            Date endDate
    );

}
