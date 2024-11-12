package com.masferrer.models.dtos;

import com.masferrer.models.entities.Student;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StudentAbsenceCountDTO {
    private Student student;
    private CustomClassroomDTO classroom;
    private Long unjustifiedAbsences;
    private Long justifiedAbsences;
    private Long totalAbsences;

    public StudentAbsenceCountDTO(Student student, Long unjustifiedAbsences, Long justifiedAbsences, Long totalAbsences) {
        this.student = student;
        this.unjustifiedAbsences = unjustifiedAbsences;
        this.justifiedAbsences = justifiedAbsences;
        this.totalAbsences = totalAbsences;
    }
}