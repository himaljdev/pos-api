package com.billing.service.dto.response;

import lombok.Data;

@Data
public class StockUpdateDTO {
    private Long stockId;
    private Integer newQty;
} 