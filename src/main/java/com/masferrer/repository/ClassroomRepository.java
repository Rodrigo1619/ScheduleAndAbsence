package com.masferrer.repository;

import java.util.UUID;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.masferrer.models.entities.Classroom;
import com.masferrer.models.entities.Grade;
import com.masferrer.models.entities.Shift;
import com.masferrer.models.entities.User;

public interface ClassroomRepository extends JpaRepository<Classroom, UUID>{
    Classroom findByYearAndGradeAndShiftAndUser(String year, Grade grade, Shift shift, User teacher);
    Classroom findByYearAndGradeAndShift(String year, Grade grade, Shift shift);

    @Query("SELECT c FROM Classroom c WHERE c.year = :year AND c.grade = :grade AND c.shift = :shift AND c.id <> :id")
    Classroom findByYearAndGradeAndShiftAndNotId(@Param("year") String year, @Param("grade") Grade grade, @Param("shift") Shift shift, @Param("id") UUID id);
    
    List<Classroom> findByShiftAndYear(Shift shift, String year, Sort sort);
    List<Classroom> findByUserAndYearAndShift(User teacher, String year, Shift shift);
    List<Classroom> findByUserAndYear(User teacher, String year);
    Classroom findByUserId(UUID userId);
    List<Classroom> findAllByUserId(UUID userId);
}
