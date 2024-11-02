package com.masferrer.models.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SaveCodeDTO {


    @NotBlank(message = "Description is required")
    private String description;
}
