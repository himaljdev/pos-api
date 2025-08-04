package com.returns.service.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CustomerReturnRequestItemDTO{
    private Long billingId;
    private BigDecimal qty;
}
