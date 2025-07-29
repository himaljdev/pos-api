package com.auth.service.repository;


import com.auth.service.model.CashInOut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;


@Repository
public interface CashInOutRepository extends JpaRepository<CashInOut, Long> {

        CashInOut findTopByCashierUser_UsernameAndCreatedDateBetweenAndCashInOutOrderByCreatedDateDesc(String username, Date startDate, Date endDate, com.auth.service.enums.CashInOut cashInOut);

}
