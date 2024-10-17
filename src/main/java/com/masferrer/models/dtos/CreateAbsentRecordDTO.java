package com.masferrer.models.dtos;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateAbsentRecordDTO {
    private LocalDate date;
    private UUID id_classroom;
    private Integer maleAttendance;
    private Integer femaleAttendance;
    private List<AbsentStudentDTO> absentStudents;
}
