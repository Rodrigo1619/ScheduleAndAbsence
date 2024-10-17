package com.masferrer.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.masferrer.models.entities.Grade;

public interface GradeRepository extends JpaRepository<Grade, UUID>{
    Grade findByName(String name);
    Grade findByIdOrName(UUID id, String name);
    Grade findByNameAndSectionOrIdGoverment(String name, String section, String idGoverment);
}
