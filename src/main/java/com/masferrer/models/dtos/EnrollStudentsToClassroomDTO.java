package com.masferrer.models.dtos;

import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnrollStudentsToClassroomDTO {

    @NotNull(message = "A list of enrollments is required")
    private List<UUID> idEnrollments;

    @NotNull(message = "Classroom is required")
    private UUID idClassroom;
}
