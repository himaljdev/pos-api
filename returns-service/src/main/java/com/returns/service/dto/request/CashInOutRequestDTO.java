package com.returns.service.dto.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CashInOutRequestDTO extends ChannelRequestDTO{
    private String cashInOut;
    private String remark;
    private Double amount;
}
