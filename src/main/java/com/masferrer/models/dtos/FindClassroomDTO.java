package com.masferrer.models.dtos;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FindClassroomDTO {

    @NotBlank(message = "Year is required")
    @Positive(message = "Year must be positive")
    private String year;

    @NotNull(message = "Grade is required")
    private UUID idGrade;

    @NotNull(message = "Shift is required")
    private UUID idShift;
}
