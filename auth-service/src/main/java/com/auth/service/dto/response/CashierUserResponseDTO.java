/**
 * User: Himal_J
 * Date: 2/23/2025
 * Time: 7:52 PM
 * <p>
 */

package com.auth.service.dto.response;

import com.auth.service.dto.SimpleBaseDTO;
import lombok.Data;

import java.util.Date;


@Data
public class CashierUserResponseDTO {
    private String accessToken;
    private String username;
    private String email;
    private String mobile;
    private boolean reset;
    private String status;
    private String statusDescription;
    private String lastLoggedChannel;
    private String lastLoggedChannelDescription;
    private Date lastPasswordChangeDate;
    private Date lastLoggedDate;
    private Date mbLastLoggedDate;
    private Date opLastLoggedDate;
    private boolean expectingFirstTimeLogging;
    private Date passwordExpiredDate;
    private SimpleBaseDTO location;
    private boolean opening;
}
