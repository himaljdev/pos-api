package com.billing.service.dto.response;

import com.billing.service.dto.SimpleBaseDTO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class BillingResponseDTO {
    private Long id;
    private String invoiceNumber;
    private String paymentType;
    private String paymentTypeDescription;
    private String customerName;
    private String customerMobile;
    private SimpleBaseDTO location;
    private String salesType;
    private String salesTypeDescription;
    private BigDecimal totalAmount;
    private BigDecimal payAmount;
    private String remark;
    private Date createDate;
}
