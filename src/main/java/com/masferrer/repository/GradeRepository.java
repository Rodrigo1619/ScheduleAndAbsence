package com.masferrer.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.masferrer.models.entities.Grade;
import com.masferrer.models.entities.Shift;

public interface GradeRepository extends JpaRepository<Grade, UUID>{
    Grade findByName(String name);
    Grade findByIdOrName(UUID id, String name);

    @Query("SELECT g FROM Grade g WHERE (g.name = :name AND g.shift = :shift AND g.section = :section) OR g.idGoverment = :idGoverment")
    Grade findByNameAndShiftAndSectionOrIdGoverment(@Param("name") String name, @Param("shift") Shift shift, @Param("section") String section, @Param("idGoverment") String idGoverment);
    
    @Query("SELECT g FROM Grade g WHERE (g.name = :name AND g.shift = :shift AND g.section = :section OR g.idGoverment = :idGoverment) AND g.id <> :id")
    Grade findByNameAndShiftAndSectionOrIdGovermentAndNotId(@Param("name") String name, @Param("shift") Shift shift, @Param("section") String section, @Param("idGoverment") String idGoverment, @Param("id") UUID id);

    @Query("SELECT g FROM Grade g WHERE g.shift = :shift ORDER BY g.name ASC, g.section ASC")
    List<Grade> findByShift(@Param("shift") Shift shift);
}
