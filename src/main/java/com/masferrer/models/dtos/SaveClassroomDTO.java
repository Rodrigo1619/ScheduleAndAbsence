package com.masferrer.models.dtos;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SaveClassroomDTO {

    @NotBlank(message = "year is required")
    private String year;

    @NotNull(message = "idGrade is required")
    private UUID idGrade;

    @NotNull(message = "idTeacher is required")
    private UUID idTeacher;
}
