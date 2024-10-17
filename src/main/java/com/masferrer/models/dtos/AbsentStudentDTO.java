package com.masferrer.models.dtos;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AbsentStudentDTO {
    private UUID id_student;
    private UUID id_absence_record;
    private String comments;
    
}
