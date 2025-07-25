package com.returns.service.dto.response;
import com.returns.service.dto.SimpleBaseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemResponseDTO  {
    private Long id;
    private String code;
    private String description;
    private String status;
    private String statusDescription;
    private SimpleBaseDTO category;
    private SimpleBaseDTO brand;
    private String unit;
    private String unitDescription;
}
