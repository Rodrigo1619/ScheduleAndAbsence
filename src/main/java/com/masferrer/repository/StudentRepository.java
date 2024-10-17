package com.masferrer.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.masferrer.models.entities.Student;
import java.util.List;


public interface StudentRepository extends JpaRepository<Student, UUID>{
    Student findByNie(String nie);
    Student findByNieOrName(String nie, String name);
    List<Student> findAllByNieOrName(String nie, String name);
}
