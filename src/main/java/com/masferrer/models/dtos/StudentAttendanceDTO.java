package com.masferrer.models.dtos;

import java.time.LocalDate;
import java.util.UUID;

import com.masferrer.models.entities.Code;
import com.masferrer.models.entities.Student;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentAttendanceDTO {
    private UUID id;
    private LocalDate date;
    private String comments;
    private Student student;
    private Code code;
}
