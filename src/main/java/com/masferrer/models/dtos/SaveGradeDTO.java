package com.masferrer.models.dtos;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SaveGradeDTO {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "idGoverment is required")
    private String idGoverment;

    @NotBlank(message = "section is required")
    private String section;

    @NotNull(message = "idShift is required")
    private UUID idShift;
}
