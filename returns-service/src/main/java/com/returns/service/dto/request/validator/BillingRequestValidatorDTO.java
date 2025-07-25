package com.returns.service.dto.request.validator;


import com.returns.service.enums.PaymentType;
import com.returns.service.enums.SalesType;
import com.returns.service.validator.ValidEnum;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
//@Conditional(selected = "salesType", values = {"NORMAL"}, required = {"salesPrice"}, message = "Sales price is required")
//@Conditional(selected = "salesType", values = {"WHOLESALE"}, required = {"salesDiscount"}, message = "Sales discount price is required")
public class BillingRequestValidatorDTO extends ChannelRequestValidatorDTO {
    @NotBlank(message = "Cashier is required")
    private String cashierUser;
    @NotBlank(message = "Payment type is required")
    @ValidEnum(enumClass = PaymentType.class, message = "Invalid payment type")
    private String paymentType;
    @NotNull(message = "Customer is required")
    private Long customer;
    @NotBlank(message = "Sales type is required")
    @ValidEnum(enumClass = SalesType.class, message = "Invalid sales type")
    private String salesType;
    @NotNull(message = "Total amount is required")
    @DecimalMin(value = "0.01", inclusive = true, message = "Total amount must be greater than 0")
    @Positive(message = "Total Amount must be a positive value")
    private BigDecimal totalAmount;
    @NotNull(message = "Pay amount is required")
    @Positive(message = "Pay Amount must be a positive value")
    @DecimalMin(value = "0.00", inclusive = true, message = "Pay amount must be greater than 0")
    private BigDecimal payAmount;
    private String remark;
    @NotNull(message = "Billing(s) item is required")
    @NotEmpty(message = "Billing(s) document is required")
    @Valid
    private List<BillingItemRequestValidatorDTO> billingItem;

}
