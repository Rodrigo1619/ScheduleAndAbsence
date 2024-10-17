package com.masferrer.models.dtos;

import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateCodeDTO {

    @Positive(message = "Number must be positive")
    private String number;

    private String description;
}
