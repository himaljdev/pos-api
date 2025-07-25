package com.returns.service.dto.response;

import lombok.Data;

import java.util.Date;

@Data
public class InvoiceResponseDTO {
    private String invoiceNumber;
    private String counter;
    private String paymentType;
    private String paymentTypeDescription;
    private String customerName;
    private String outletName;
    private String salesType;
    private String salesTypeDescription;
    private Date invoiceDate;
}
