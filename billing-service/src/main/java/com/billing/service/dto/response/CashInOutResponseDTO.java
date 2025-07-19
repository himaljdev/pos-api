package com.billing.service.dto.response;


import lombok.Data;

import java.util.Date;

@Data
public class CashInOutResponseDTO  {
    private Long id;
    private Date date;
    private String cashInOut;
    private String cashInOutDescription;
    private String remark;
    private Double amount;
}
