package com.masferrer.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.masferrer.models.entities.Grade;
import com.masferrer.models.entities.Shift;

public interface GradeRepository extends JpaRepository<Grade, UUID>{
    Grade findByName(String name);
    Grade findByIdOrName(UUID id, String name);

    @Query("SELECT g FROM Grade g ORDER BY " +
    "CASE " +
    "  WHEN g.name LIKE '1ro%' THEN 1 " +
    "  WHEN g.name LIKE '2do%' THEN 2 " +
    "  WHEN g.name LIKE '3ro%' THEN 3 " +
    "  ELSE 4 " +
    "END, " +
    "g.name ASC, g.section ASC, g.shift.name ASC")
    List<Grade> findAllSorted();

    @Query("SELECT g FROM Grade g ORDER BY " +
    "CASE " +
    "  WHEN g.name LIKE '1ro%' THEN 1 " +
    "  WHEN g.name LIKE '2do%' THEN 2 " +
    "  WHEN g.name LIKE '3ro%' THEN 3 " +
    "  ELSE 4 " +
    "END, " +
    "g.name ASC, g.section ASC, g.shift.name ASC")
    Page<Grade> findAllSorted(Pageable pageable);

    @Query("SELECT g FROM Grade g WHERE (g.name = :name AND g.shift = :shift AND g.section = :section) OR g.idGoverment = :idGoverment")
    Grade findByNameAndShiftAndSectionOrIdGoverment(@Param("name") String name, @Param("shift") Shift shift, @Param("section") String section, @Param("idGoverment") String idGoverment);
    
    @Query("SELECT g FROM Grade g WHERE (g.name = :name AND g.shift = :shift AND g.section = :section OR g.idGoverment = :idGoverment) AND g.id <> :id")
    Grade findByNameAndShiftAndSectionOrIdGovermentAndNotId(@Param("name") String name, @Param("shift") Shift shift, @Param("section") String section, @Param("idGoverment") String idGoverment, @Param("id") UUID id);

    @Query("SELECT g FROM Grade g WHERE g.shift.id = :shiftId " +
    "ORDER BY " +
    "CASE " +
    "  WHEN g.name LIKE '1ro%' THEN 1 " +
    "  WHEN g.name LIKE '2do%' THEN 2 " +
    "  WHEN g.name LIKE '3ro%' THEN 3 " +
    "  ELSE 4 " +
    "END, " +
    "g.name ASC, g.section ASC")
    List<Grade> findByShift(@Param("shiftId") UUID shiftId);
}
