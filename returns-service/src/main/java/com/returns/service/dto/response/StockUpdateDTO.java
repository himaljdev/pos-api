package com.returns.service.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class StockUpdateDTO {
    private Long stockId;
    private BigDecimal newQty;
} 