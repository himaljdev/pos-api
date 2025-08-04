package com.returns.service.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ReturnsItemResponseDTO {
    private Long id;
    private BigDecimal qty;

}
