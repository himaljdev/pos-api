package com.returns.service.dto.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class CustomerReturnRequestDTO extends ChannelRequestDTO{
    private String invoiceNo;
    private List<CustomerReturnRequestItemDTO> customerReturnRequestItemDTOList;
    private String remark;
}
