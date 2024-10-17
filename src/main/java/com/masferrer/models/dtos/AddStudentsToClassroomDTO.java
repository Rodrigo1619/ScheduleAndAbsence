package com.masferrer.models.dtos;

import java.util.List;
import java.util.UUID;

import com.masferrer.models.entities.Student;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AddStudentsToClassroomDTO {

    @NotNull(message = "Students is required")
    private List<Student> students;

    @NotNull(message = "GradeId is required")
    private UUID GradeId;

    @NotNull(message = "ShiftId is required")
    private UUID ShiftId;

    @NotBlank(message = "Year is required")
    @Positive(message = "Year must be positive")
    private String Year;
}
