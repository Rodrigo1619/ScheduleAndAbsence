package com.masferrer.models.dtos;

import java.util.UUID;

import com.masferrer.models.entities.Shift;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomClassroomDTO {

    private UUID id;
    private String year;
    private ShowGradeConcatDTO grade;
    private Shift shift;
    private ShortUserDTO homeroomTeacher;
}
