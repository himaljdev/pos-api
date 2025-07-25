package com.returns.service.dto.request;

import lombok.Data;

import java.math.BigDecimal;


@Data
public class BillingItemRequestDTO {
    private int qty;
    private BigDecimal salesPrice;
    private Double salesDiscount;
    private Long stock;
}
