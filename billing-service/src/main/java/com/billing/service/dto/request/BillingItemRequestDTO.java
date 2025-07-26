package com.billing.service.dto.request;

import lombok.Data;

import java.math.BigDecimal;
import java.math.BigDecimal;


@Data
public class BillingItemRequestDTO {
    private BigDecimal qty;
    private BigDecimal salesPrice;
    private Double salesDiscount;
    private Long stock;
    private Boolean other;
}
