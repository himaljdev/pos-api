package com.billing.service.dto.request.validator;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;


@Data
public class BillingItemRequestValidatorDTO {
    @NotNull(message = "Qty is required")
    @Min(value = 1, message = "Qty must be at least 1")
    private BigDecimal qty;
    private BigDecimal salesPrice;
    @NotNull(message = "Stock is required")
    private Long stock;
    @NotNull(message = "Item state required")
    private Boolean other;
}
