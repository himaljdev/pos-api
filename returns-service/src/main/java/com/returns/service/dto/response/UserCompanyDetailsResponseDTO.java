/**
 * User: Himal_J
 * Date: 2/20/2025
 * Time: 1:54 PM
 * <p>
 */

package com.returns.service.dto.response;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.returns.service.dto.SimpleBaseDTO;
import lombok.Data;

import java.util.Date;

@Data
public class UserCompanyDetailsResponseDTO {
    private SimpleBaseDTO companyTypes;
    private SimpleBaseDTO staffCategories;
    private SimpleBaseDTO staffTypes;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Colombo")
    private Date permanentDate;
    private Date terminateDate;
    private String designation;
    private SimpleBaseDTO insurancePolicy;
    private String facility;
    private String facilityDescription;
}
