package com.billing.service.dto.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class BillingRequestDTO extends ChannelRequestDTO{
    private String cashierUser;
    private String paymentType;
    private Long customer;
    private String salesType;
    private BigDecimal totalAmount;
    private BigDecimal payAmount;
    private String remark;
    private List<BillingItemRequestDTO> billingItem;
}
