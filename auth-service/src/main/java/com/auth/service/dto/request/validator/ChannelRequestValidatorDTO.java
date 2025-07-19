/**
 * User: Himal_J
 * Date: 2/3/2025
 * Time: 12:23 PM
 * <p>
 */

package com.auth.service.dto.request.validator;


import com.auth.service.enums.Channel;
import com.auth.service.enums.Messages;
import com.auth.service.validator.Conditional;
import com.auth.service.validator.OnLogin;
import com.auth.service.validator.ValidEnum;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Conditional(selected = "message" , values = {"LOGIN"} ,required = {"username"} ,message = "Username is required",groups = {OnLogin.class})
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
