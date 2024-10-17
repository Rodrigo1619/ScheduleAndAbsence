package com.masferrer.models.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SaveGradeDTO {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "idGoverment is required")
    private String idGoverment;

    @NotBlank(message = "section is required")
    private String section;
}
