package com.masferrer.models.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SaveStudentDTO {

    @NotBlank(message = "Nie is required")
    @Positive(message = "Nie must be positive")
    private String nie;

    @NotBlank(message = "Name is required")
    private String name;
}
