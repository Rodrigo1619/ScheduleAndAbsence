package com.masferrer.services.implementations;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.masferrer.models.entities.ClassPeriod;
import com.masferrer.repository.ClassPeriodRepository;
import com.masferrer.services.ClassPeriodService;
import com.masferrer.utils.NotFoundException;

@Service
public class ClassPeriodServiceImpl implements ClassPeriodService {
    
    @Autowired
    private ClassPeriodRepository classPeriodRepository;

    @Override
    public List<ClassPeriod> findAll() {
        return classPeriodRepository.findAll();
    }

    @Override
    public ClassPeriod findById(UUID id) {
        return classPeriodRepository.findById(id).orElseThrow(() -> new NotFoundException("Class period not found"));
    }

}
