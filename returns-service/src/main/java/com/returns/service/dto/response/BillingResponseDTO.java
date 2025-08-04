package com.returns.service.dto.response;

import com.returns.service.dto.SimpleBaseDTO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class BillingResponseDTO {
    private Long id;
    private String invoiceNumber;
    private CashierUserResponseDTO cashierUser;
    private String paymentType;
    private String paymentTypeDescription;
    private CustomerResponseDTO customer;
    private SimpleBaseDTO location;
    private String salesType;
    private String salesTypeDescription;
    private BigDecimal totalAmount;
    private BigDecimal payAmount;
    private String remark;
    private Date createdDate;
}
