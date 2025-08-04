package com.billing.service.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class CustomerResponseDTO {
    private Long id;
    private String name;
    private String status;
    private String statusDescription;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String title;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String titleDescription;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String city;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String telNo;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String email;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String fundLimit;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private BigDecimal pendingBalance;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private BigDecimal totalBalance;

}
