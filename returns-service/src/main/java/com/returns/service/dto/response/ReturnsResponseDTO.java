package com.returns.service.dto.response;
import com.returns.service.dto.SimpleBaseDTO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class ReturnsResponseDTO {
    private Long id;
    private String invoiceNumber;
    private String remark;
    private BigDecimal totalAmount;
    private String customerName;
    private String customerMobile;
    private SimpleBaseDTO location;
    private Date createDate;
}
