package com.billing.service.dto.request.validator;

import com.billing.service.enums.CashInOut;
import com.billing.service.validator.ValidEnum;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CashInOutRequestValidatorDTO extends ChannelRequestValidatorDTO{
    @NotBlank(message = "Cash In/Out type is required")
    @ValidEnum(enumClass = CashInOut.class,message = "Invalid cash In/Out type")
    private String cashInOut;
    @NotBlank(message = "Remark is required")
    private String remark;
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.00", inclusive = true, message = "Amount must be greater than 0")
    @Positive(message = "Amount must be a positive value")
    private Double amount;
}
