package com.masferrer.repository;

import java.util.UUID;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.masferrer.models.entities.Classroom;

public interface ClassroomRepository extends JpaRepository<Classroom, UUID>{
    @Query("SELECT c FROM Classroom c WHERE c.year = :year AND c.grade.id = :gradeId AND c.user.id = :userId")
    Classroom findByYearAndGradeAndUser(@Param("year") String year, @Param("gradeId") UUID gradeId, @Param("userId") UUID userId);

    @Query("SELECT c FROM Classroom c WHERE c.year = :year AND c.grade.id = :gradeId")
    Classroom findByYearAndGrade(@Param("year") String year, @Param("gradeId") UUID gradeId);

    @Query("SELECT c FROM Classroom c WHERE c.year = :year AND c.grade.id = :gradeId AND c.id <> :id")
    Classroom findByYearAndGradeAndNotId(@Param("year") String year, @Param("gradeId") UUID gradeId, @Param("id") UUID id);
    
    // List<Classroom> findByShiftAndYear(Shift shift, String year, Sort sort);
    @Query("SELECT c FROM Classroom c WHERE c.year = :year AND c.grade.shift.id = :shiftId ORDER BY c.grade.name ASC, c.grade.section ASC")
    List<Classroom> findByShiftAndYear(@Param("shiftId") UUID shiftId, @Param("year") String year);

    @Query("SELECT c FROM Classroom c WHERE c.year = :year AND c.grade.shift.id = :shiftId AND c.user.id = :userId ORDER BY c.grade.name ASC, c.grade.section ASC")
    List<Classroom> findByUserAndYearAndShift(@Param("userId") UUID userId, @Param("year") String year, @Param("shiftId") UUID shiftId);

    //List<Classroom> findByUserAndYear(User teacher, String year);
    @Query("SELECT c FROM Classroom c WHERE c.year = :year AND c.user.id = :userId ORDER BY c.grade.name ASC, c.grade.section ASC, c.grade.shift.name ASC")
    List<Classroom> findByUserAndYear(@Param("userId") UUID userId, @Param("year") String year);

    Classroom findByUserId(UUID userId);
    List<Classroom> findAllByUserId(UUID userId, Sort sort);
}