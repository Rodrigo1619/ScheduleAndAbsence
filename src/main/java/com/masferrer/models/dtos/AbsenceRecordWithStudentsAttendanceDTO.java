package com.masferrer.models.dtos;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AbsenceRecordWithStudentsAttendanceDTO {
    private UUID id;
    private LocalDate date;
    private Integer maleAttendance;
    private Integer femaleAttendance;
    private Boolean teacherValidation;
    private Boolean coordinationValidation;
    private CustomClassroomDTO classroom;
    private List<StudentAttendanceDTO> absentStudents;
}
