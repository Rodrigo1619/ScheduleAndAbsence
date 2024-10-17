package com.masferrer.models.dtos;

import java.util.List;

import com.masferrer.models.entities.Student;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClassroomWithStudentsDTO {
    private CustomClassroomDTO classroom;
    private List<Student> students;
}
