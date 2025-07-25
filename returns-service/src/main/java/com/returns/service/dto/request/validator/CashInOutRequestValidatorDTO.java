package com.returns.service.dto.request.validator;

import com.returns.service.enums.CashInOut;
import com.returns.service.validator.Conditional;
import com.returns.service.validator.ValidEnum;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Conditional(selected = "cashInOut" , values = {"IN","OUT"} ,required = {"remark"} ,message = "Remark is required")
public class CashInOutRequestValidatorDTO extends ChannelRequestValidatorDTO {
    @NotBlank(message = "Cash In/Out type is required")
    @ValidEnum(enumClass = CashInOut.class,message = "Invalid cash In/Out type")
    private String cashInOut;
    private String remark;
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.00", inclusive = true, message = "Amount must be greater than 0")
    @Positive(message = "Amount must be a positive value")
    private Double amount;
}
