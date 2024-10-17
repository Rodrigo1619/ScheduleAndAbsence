package com.masferrer.models.dtos;

import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateStudentDTO {

    @Positive(message = "Nie must be positive")
    private String nie;

    private String name;
}
