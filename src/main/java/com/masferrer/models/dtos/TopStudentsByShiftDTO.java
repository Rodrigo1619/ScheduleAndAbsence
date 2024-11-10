package com.masferrer.models.dtos;

import com.masferrer.models.entities.Student;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TopStudentsByShiftDTO {

    private TopStudentAbsenceDTO morningShiftTopStudent;
    private TopStudentAbsenceDTO afternoonShiftTopStudent;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TopStudentAbsenceDTO{
        private Student student;
        private CustomClassroomDTO classroom;
        private Long totalAbsences;
    }
}
