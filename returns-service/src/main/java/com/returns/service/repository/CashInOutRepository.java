package com.returns.service.repository;
import com.returns.service.enums.CashInOut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface CashInOutRepository extends JpaRepository<com.returns.service.model.CashInOut, Long> {
    List<com.returns.service.model.CashInOut> findAllByCashierUser_UsernameAndCreatedDateBetween(
            String username,
            Date startDate,
            Date endDate
    );

    boolean existsByCashierUser_UsernameAndCreatedDateBetweenAndCashInOut(String username, Date startDate, Date endDate, CashInOut cashInOut);

}
