package com.masferrer.models.dtos;

import com.masferrer.models.entities.Student;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StudentAbsenceCountDTO {
    private Student student;
    private Long unjustifiedAbsences;
    private Long justifiedAbsences;
    private Long totalAbsences;
}
