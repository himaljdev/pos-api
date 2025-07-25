package com.returns.service.dto.request.validator;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CustomerReturnRequestValidatorDTO extends ChannelRequestValidatorDTO{
    @NotBlank(message = "Invoice number is required")
    private String invoiceNo;
}
