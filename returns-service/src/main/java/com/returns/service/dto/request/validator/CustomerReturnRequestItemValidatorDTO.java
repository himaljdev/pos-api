package com.returns.service.dto.request.validator;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CustomerReturnRequestItemValidatorDTO {
    @NotNull(message = "Stock is required")
    private Long stock;
    @NotNull(message = "Qty is required")
    private BigDecimal qty;
}
