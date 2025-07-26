package com.returns.service.dto.request.validator;

import com.returns.service.validator.OnReturn;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class CustomerReturnRequestValidatorDTO extends ChannelRequestValidatorDTO{
    @NotBlank(message = "Invoice number is required")
    private String invoiceNo;
    @NotNull(message = "Return item(s) is required",groups = {OnReturn.class})
    @NotEmpty(message = "Return item(s) is required",groups = {OnReturn.class})
    @Valid
    private List<CustomerReturnRequestItemValidatorDTO> customerReturnRequestItemDTOList;
    private String remark;
    @NotNull(message = "Debit amount is required",groups = {OnReturn.class})
    @DecimalMin(value = "0.00", inclusive = true, message = "Amount must be greater than 0",groups = {OnReturn.class})
    private BigDecimal debitAmount;
}
