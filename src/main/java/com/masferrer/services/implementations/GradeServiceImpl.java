package com.masferrer.services.implementations;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.masferrer.models.dtos.SaveGradeDTO;
import com.masferrer.models.dtos.ShowGradeConcatDTO;
import com.masferrer.models.entities.Grade;
import com.masferrer.repository.GradeRepository;
import com.masferrer.services.GradeService;
import com.masferrer.utils.EntityMapper;

import jakarta.transaction.Transactional;

@Service
public class GradeServiceImpl implements GradeService{

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private EntityMapper entityMapper;

    @Override
    public List<ShowGradeConcatDTO> findAll() {
        Sort sort = Sort.by(Sort.Order.asc("name"), Sort.Order.asc("section")); // Sort by name and section
        List<Grade> grades = gradeRepository.findAll(sort);
        return grades.stream()
                    .map(entityMapper::mapGradeConcatDTO)
                    .collect(Collectors.toList());
    }

    @Override
    public Page<ShowGradeConcatDTO> findAll(int page, int size) {
        Sort sort = Sort.by(Sort.Order.asc("name"), Sort.Order.asc("section")); // Sort by name and section
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Grade> gradePage = gradeRepository.findAll(pageable);
    
        List<ShowGradeConcatDTO> showGradeConcatDTOs = gradePage.stream()
            .map(entityMapper::mapGradeConcatDTO)
            .collect(Collectors.toList());
        
        return new PageImpl<>(showGradeConcatDTOs, pageable, gradePage.getTotalElements());
    }

    @Override
    public Grade findById(UUID id) {
        return gradeRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public Boolean save(SaveGradeDTO info) throws Exception {
        Grade gradeFound = gradeRepository.findByNameAndSectionOrIdGoverment(info.getName(), info.getSection(), info.getIdGoverment());

        if (gradeFound != null) {
            return false;
        }

        Grade grade = new Grade(info.getName(), info.getIdGoverment(), info.getSection());
        gradeRepository.save(grade);
        return true;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public Boolean update(SaveGradeDTO info, UUID id) throws Exception {
        Grade gradeToUpdate = gradeRepository.findById(id).orElse(null);

        if (gradeToUpdate == null) {
            return false;
        }

        Grade gradeFound = gradeRepository.findByNameAndSectionOrIdGoverment(info.getName(), info.getSection(), info.getIdGoverment());

        if (gradeFound != null && !gradeFound.getId().equals(id)) {
            return false;
        }

        gradeToUpdate.setName(info.getName());
        gradeToUpdate.setIdGoverment(info.getIdGoverment());
        gradeToUpdate.setSection(info.getSection());
        gradeRepository.save(gradeToUpdate);
        
        return true;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public Boolean delete(UUID id) throws Exception {
        Grade gradeToDelete = gradeRepository.findById(id).orElse(null);

        if (gradeToDelete == null) {
            return false;
        }

        gradeRepository.delete(gradeToDelete);
        return true;
    }

}
