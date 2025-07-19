/**
 * User: Himal_J
 * Date: 2/4/2025
 * Time: 9:32 AM
 * <p>
 */

package com.auth.service.dto.request;


import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class LoginRequestDTO extends ChannelRequestDTO{
    private String password;
}
