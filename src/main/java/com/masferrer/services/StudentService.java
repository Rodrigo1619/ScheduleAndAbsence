package com.masferrer.services;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;

import com.masferrer.models.dtos.SaveStudentDTO;
import com.masferrer.models.dtos.UpdateStudentDTO;
import com.masferrer.models.entities.Student;

public interface StudentService {
    List<Student> findAll();
    Page<Student> findAll(int pageNo, int pageSize);
    List<Student> findNewStudents();
    Student findById(UUID id);
    Student save(SaveStudentDTO info) throws Exception;
    Student update(UpdateStudentDTO info, UUID id) throws Exception;
    Boolean delete(UUID id) throws Exception;
    Boolean toggleActiveStatus(UUID id) throws Exception;
}
