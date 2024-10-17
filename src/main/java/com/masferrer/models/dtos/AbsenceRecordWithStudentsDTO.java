package com.masferrer.models.dtos;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.masferrer.models.entities.AbsentStudent;
import com.masferrer.models.entities.Classroom;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AbsenceRecordWithStudentsDTO {
    private UUID id;
    private LocalDate date;
    private Integer maleAttendance;
    private Integer femaleAttendance;
    private Boolean teacherValidation;
    private Boolean coordinationValidation;
    private Classroom classroom;
    private List<AbsentStudent> absentStudents;

}
