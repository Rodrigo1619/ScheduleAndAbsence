package com.masferrer.models.dtos;

import java.util.UUID;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateClassroomDTO {

    private String year;
    private UUID idGrade;
    private UUID idTeacher;
}
