package com.masferrer.models.dtos;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EditAbsenceRecordDTO {
    private LocalDate updateDate;
    private Integer maleAttendance;
    private Integer femaleAttendance;
    private List<EditAbsentStudentDTO> absentStudents;
    private List<UUID> deleteAbsentStudents;
}
