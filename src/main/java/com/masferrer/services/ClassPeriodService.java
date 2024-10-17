package com.masferrer.services;

import java.util.List;
import java.util.UUID;

import com.masferrer.models.entities.ClassPeriod;

public interface ClassPeriodService {
    List<ClassPeriod> findAll();
    ClassPeriod findById(UUID id);
}
