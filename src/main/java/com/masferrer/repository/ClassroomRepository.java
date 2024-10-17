package com.masferrer.repository;

import java.util.UUID;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.masferrer.models.entities.Classroom;
import com.masferrer.models.entities.Grade;
import com.masferrer.models.entities.Shift;
import com.masferrer.models.entities.User;

public interface ClassroomRepository extends JpaRepository<Classroom, UUID>{
    Classroom findByYearAndGradeAndShiftAndUser(String year, Grade grade, Shift shift, User teacher);
    Classroom findByYearAndGradeAndShift(String year, Grade grade, Shift shift);
    List<Classroom> findByShiftAndYear(Shift shift, String year);
    List<Classroom> findByUserAndYearAndShift(User teacher, String year, Shift shift);
    List<Classroom> findByUserAndYear(User teacher, String year);
    Classroom findByUserId(UUID userId);
    List<Classroom> findAllByUserId(UUID userId);
}
