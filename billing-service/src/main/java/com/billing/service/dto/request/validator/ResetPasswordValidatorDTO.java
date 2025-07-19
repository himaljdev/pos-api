/**
 * User: Himal_J
 * Date: 2/6/2025
 * Time: 8:21 AM
 * <p>
 */

package com.billing.service.dto.request.validator;

import com.billing.service.validator.PasswordEquals;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@PasswordEquals(message = "New password and confirm password mismatch.Please try again")
public class ResetPasswordValidatorDTO extends ChannelRequestValidatorDTO {
    @NotBlank(message = "New password cannot be empty")
    private String password;
    @NotBlank(message = "Confirm password cannot be empty")
    private String confirmPassword;
}
