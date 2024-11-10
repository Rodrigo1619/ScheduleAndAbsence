package com.masferrer.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.masferrer.models.entities.Shift;

public interface ShiftRepository extends JpaRepository<Shift, UUID>{

    Shift findByName(String name);
}
