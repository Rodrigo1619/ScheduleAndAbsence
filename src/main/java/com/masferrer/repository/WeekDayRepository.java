package com.masferrer.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.masferrer.models.entities.Weekday;

public interface WeekDayRepository extends JpaRepository<Weekday, UUID>{
    Weekday findByDay(String day);
}
