/**
 * User: Himal_J
 * Date: 2/3/2025
 * Time: 12:23 PM
 * <p>
 */

package com.billing.service.dto.request.validator;



import com.billing.service.enums.Channel;
import com.billing.service.enums.Messages;
import com.billing.service.validator.Conditional;
import com.billing.service.validator.ValidEnum;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Conditional(selected = "message" , values = {"CHECKOUT","CASH_IN_OUT",
        "CASH_IN_OUT_VIEW","STOCK_FILTER_LIST",
        "REFERENCE_DATA","LATEST_INVOICE","TODAY_SALES"} ,required = {"username"} ,message = "Username is required")
public class ChannelRequestValidatorDTO {
    @NotBlank(message = "Channel is required")
    @ValidEnum(enumClass = Channel.class,message = "Invalid channel")
    private String channel;
    @NotBlank(message = "IP is required")
    private String ip;
    @NotBlank(message = "Message is required")
    @ValidEnum(enumClass = Messages.class, message = "Invalid message")
    private String message;
    private String username;
}
