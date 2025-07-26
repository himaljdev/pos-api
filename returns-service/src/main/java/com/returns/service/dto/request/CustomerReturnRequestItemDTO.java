package com.returns.service.dto.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
public class CustomerReturnRequestItemDTO extends ChannelRequestDTO{
    private String itemCode;
    private BigDecimal qty;
}
