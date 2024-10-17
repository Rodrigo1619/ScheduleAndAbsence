package com.masferrer.models.dtos;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EditAbsentStudentDTO {
    private UUID id_student;
    private UUID id_code;
    private String comments;

}
