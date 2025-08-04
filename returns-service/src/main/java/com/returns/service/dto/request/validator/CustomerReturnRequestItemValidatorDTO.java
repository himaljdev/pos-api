package com.returns.service.dto.request.validator;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CustomerReturnRequestItemValidatorDTO {
    @NotNull(message = "Billing id is required")
    private Long billingId;
    @NotNull(message = "Qty is required")
    private BigDecimal qty;
}
