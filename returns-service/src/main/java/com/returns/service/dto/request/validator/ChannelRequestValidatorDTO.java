/**
 * User: Himal_J
 * Date: 2/3/2025
 * Time: 12:23 PM
 * <p>
 */

package com.returns.service.dto.request.validator;

import com.returns.service.enums.Channel;
import com.returns.service.enums.Messages;
import com.returns.service.validator.Conditional;
import com.returns.service.validator.ValidEnum;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Conditional(selected = "message" , values = {"CHECKOUT","CASH_IN_OUT","CASH_IN_OUT_VIEW","STOCK_FILTER_LIST","REFERENCE_DATA","VIEW","FILTER_LIST"} ,required = {"username"} ,message = "Username is required")
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
