package com.returns.service.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BillingDetailResponseDTO {
    private Long id;
    private BigDecimal qty;
    private BigDecimal returnsQty;
    private BigDecimal salesPrice;
    private Double salesDiscount;
    private BigDecimal itemCost;
    private BigDecimal lablePrice;
    private BigDecimal retailPrice;
    private BigDecimal wholesalePrice;
    private Integer retailDiscount;
    private Integer wholesaleDiscount;
    private StockResponseDTO stock;
}
