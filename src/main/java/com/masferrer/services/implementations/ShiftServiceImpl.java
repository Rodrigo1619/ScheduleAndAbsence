package com.masferrer.services.implementations;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.masferrer.models.entities.Shift;
import com.masferrer.repository.ShiftRepository;
import com.masferrer.services.ShiftService;

@Service
public class ShiftServiceImpl implements ShiftService{

    @Autowired
    private ShiftRepository shiftRepository;

    @Override
    public List<Shift> findAll() {
        return shiftRepository.findAll();
    }

    @Override
    public Shift findById(UUID id) {
        return shiftRepository.findById(id).orElse(null);
    }

}
