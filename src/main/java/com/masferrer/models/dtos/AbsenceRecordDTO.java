package com.masferrer.models.dtos;

import java.util.UUID;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AbsenceRecordDTO {
    private UUID id;
    private LocalDate date;
    private Integer maleAttendance;
    private Integer femaleAttendance;
    private Boolean teacherValidation;
    private Boolean coordinationValidation;
    private CustomClassroomDTO classroom;

    public AbsenceRecordDTO( LocalDate date, CustomClassroomDTO classroom, Integer maleAttendance, Integer femaleAttendance) {
        this.date = date;
        this.classroom = classroom;
        this.maleAttendance = maleAttendance;
        this.femaleAttendance = femaleAttendance;
    }
}
