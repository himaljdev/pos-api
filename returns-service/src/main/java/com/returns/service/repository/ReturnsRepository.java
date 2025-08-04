package com.returns.service.repository;

import com.returns.service.model.Returns;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ReturnsRepository extends JpaRepository<Returns, Long>, JpaSpecificationExecutor<Returns> {
}
