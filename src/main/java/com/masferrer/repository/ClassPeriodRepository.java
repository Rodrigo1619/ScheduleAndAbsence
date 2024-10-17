package com.masferrer.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.masferrer.models.entities.ClassPeriod;

public interface ClassPeriodRepository extends JpaRepository<ClassPeriod, UUID> {

}
