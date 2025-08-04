package com.returns.service.dto.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TodayReturnsRequestDTO extends ChannelRequestDTO{
    private Long id;
}
