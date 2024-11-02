package com.masferrer.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.masferrer.models.entities.Code;

public interface CodeRepository extends JpaRepository<Code, UUID>{
    Code findByDescription(String description);
}
