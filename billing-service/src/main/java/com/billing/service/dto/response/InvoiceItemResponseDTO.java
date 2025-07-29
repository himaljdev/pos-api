package com.billing.service.dto.response;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class InvoiceItemResponseDTO {
    private BigDecimal qty;
    private BigDecimal salesPrice;
    private Double salesDiscount;
    private String item;
    private BigDecimal total;
}
