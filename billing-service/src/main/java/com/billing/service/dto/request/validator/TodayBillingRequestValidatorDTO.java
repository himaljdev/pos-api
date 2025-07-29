package com.billing.service.dto.request.validator;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TodayBillingRequestValidatorDTO extends ChannelRequestValidatorDTO {
    @NotNull(message = "Billing id required")
    private Long id;
}
