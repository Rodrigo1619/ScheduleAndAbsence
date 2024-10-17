package com.masferrer.models.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SaveCodeDTO {

    @NotBlank(message = "Number is required")
    @Positive(message = "Number must be positive")
    private String number;

    @NotBlank(message = "Description is required")
    private String description;
}
