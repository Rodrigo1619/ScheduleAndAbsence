package com.masferrer.models.dtos;

import java.util.UUID;

import com.masferrer.models.entities.Student;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentXClassroomDTO {
    private UUID id;
    private Student student;
    private CustomClassroomDTO classroom;
    private Boolean assigned;
}
