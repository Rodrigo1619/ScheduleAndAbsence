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
    Classroom findByYearAndGradeAndUser(String year, Grade grade, User teacher);
    Classroom findByYearAndGrade(String year, Grade grade);

    @Query("SELECT c FROM Classroom c WHERE c.year = :year AND c.grade = :grade AND c.id <> :id")
    Classroom findByYearAndGradeAndNotId(@Param("year") String year, @Param("grade") Grade grade, @Param("id") UUID id);
    
    // List<Classroom> findByShiftAndYear(Shift shift, String year, Sort sort);
    @Query("SELECT c FROM Classroom c WHERE c.year = :year AND c.grade.shift = :shift ORDER BY c.grade.name ASC, c.grade.section ASC")
    List<Classroom> findByShiftAndYear(@Param("shift") Shift shift, @Param("year") String year);

    //List<Classroom> findByUserAndYearAndShift(User teacher, String year, Shift shift);
    @Query("SELECT c FROM Classroom c WHERE c.year = :year AND c.grade.shift = :shift AND c.user = :user ORDER BY c.grade.name ASC, c.grade.section ASC")
    List<Classroom> findByUserAndYearAndShift(@Param("user") User user, @Param("year") String year, @Param("shift") Shift shift);

    //List<Classroom> findByUserAndYear(User teacher, String year);
    @Query("SELECT c FROM Classroom c WHERE c.year = :year AND c.user = :user ORDER BY c.grade.name ASC, c.grade.section ASC, c.grade.shift.name ASC")
    List<Classroom> findByUserAndYear(@Param("user") User user, @Param("year") String year);

    Classroom findByUserId(UUID userId);
    List<Classroom> findAllByUserId(UUID userId, Sort sort);
}
