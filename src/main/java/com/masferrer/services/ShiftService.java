package com.masferrer.services;

import java.util.List;
import java.util.UUID;

import com.masferrer.models.entities.Shift;

public interface ShiftService {
    List<Shift> findAll();
    Shift findById(UUID id);
}
