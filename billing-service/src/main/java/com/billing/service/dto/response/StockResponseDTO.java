package com.billing.service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockResponseDTO {
    private Long id;
    private ItemResponseDTO item;
    private BigDecimal lablePrice = BigDecimal.ZERO;
    private BigDecimal itemCost =  BigDecimal.ZERO;
    private BigDecimal retailPrice = BigDecimal.ZERO;
    private BigDecimal wholesalePrice = BigDecimal.ZERO;
    private Integer retailDiscount = 0;
    private Integer wholesaleDiscount = 0;
    private BigDecimal qty = BigDecimal.valueOf(0);
    private String status;
    private String statusDescription;
}