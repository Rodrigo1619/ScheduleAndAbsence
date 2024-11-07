package com.masferrer.models.dtos;

import java.time.LocalDate;

import com.masferrer.models.entities.Code;
import com.masferrer.models.entities.Student;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentAttendanceDTO {
    private LocalDate date;
    private String comments;
    private Student student;
    private Code code;
}
