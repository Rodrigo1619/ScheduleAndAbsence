package com.masferrer.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.masferrer.models.entities.Subject;

public interface SubjectRepository extends JpaRepository<Subject, UUID>{

    Subject findByName(String name);
    Subject findOneById(UUID id);
    
}
