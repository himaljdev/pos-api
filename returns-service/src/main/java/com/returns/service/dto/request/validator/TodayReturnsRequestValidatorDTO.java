package com.returns.service.dto.request.validator;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TodayReturnsRequestValidatorDTO extends ChannelRequestValidatorDTO {
    @NotNull(message = "Returns id required")
    private Long id;
}
