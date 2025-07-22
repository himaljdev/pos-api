package com.billing.service.dto.response;

import lombok.Data;

@Data
public class CustomerResponseDTO {
    private Long id;
    private String name;
    private String status;
    private String statusDescription;
}
