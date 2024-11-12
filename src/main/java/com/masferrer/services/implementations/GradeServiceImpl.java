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
import com.masferrer.models.dtos.UpdateGradeDTO;
import com.masferrer.models.entities.Grade;
import com.masferrer.models.entities.Shift;
import com.masferrer.repository.GradeRepository;
import com.masferrer.repository.ShiftRepository;
import com.masferrer.services.GradeService;
import com.masferrer.utils.EntityMapper;
import com.masferrer.utils.NotFoundException;

import jakarta.transaction.Transactional;

@Service
public class GradeServiceImpl implements GradeService{

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private ShiftRepository shiftRepository;

    @Autowired
    private EntityMapper entityMapper;

    @Override
    public List<ShowGradeConcatDTO> findAll() {
        List<Grade> grades = gradeRepository.findAllSorted();
        return grades.stream()
                    .map(entityMapper::mapGradeConcatDTO)
                    .collect(Collectors.toList());
    }

    @Override
    public Page<ShowGradeConcatDTO> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Grade> gradePage = gradeRepository.findAllSorted(pageable);
    
        List<ShowGradeConcatDTO> showGradeConcatDTOs = gradePage.stream()
            .map(entityMapper::mapGradeConcatDTO)
            .collect(Collectors.toList());
        
        return new PageImpl<>(showGradeConcatDTOs, pageable, gradePage.getTotalElements());
    }

    @Override
    public ShowGradeConcatDTO findById(UUID id) {
        Grade result =  gradeRepository.findById(id).orElseThrow( () -> new NotFoundException("Grade not found"));
        return entityMapper.mapGradeConcatDTO(result);
    }

    @Override
    public List<ShowGradeConcatDTO> findByShift(UUID shiftId) {
        if(!shiftRepository.existsById(shiftId)){
            throw new NotFoundException("Shift not found");
        }
        List<Grade> grades = gradeRepository.findByShift(shiftId);
        return grades.stream()
                    .map(entityMapper::mapGradeConcatDTO)
                    .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public ShowGradeConcatDTO save(SaveGradeDTO info) throws Exception {
        Shift shiftFound = shiftRepository.findById(info.getIdShift()).orElseThrow( () -> new NotFoundException("Shift not found"));

        Grade gradeFound = gradeRepository.findByNameAndShiftAndSectionOrIdGoverment(info.getName(), shiftFound, info.getSection(), info.getIdGoverment());

        if (gradeFound != null) {
            throw new IllegalArgumentException("Grade already exists");
        }

        Grade newGrade = new Grade(info.getName(), info.getIdGoverment(), info.getSection(), shiftFound);
        newGrade = gradeRepository.save(newGrade);
        
        return entityMapper.mapGradeConcatDTO(newGrade); 
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public ShowGradeConcatDTO update(UUID id, UpdateGradeDTO info) throws Exception {
        Grade gradeToUpdate = gradeRepository.findById(id).orElseThrow( () -> new NotFoundException("Grade not found"));

        if(info.getIdShift() != null){
            Shift shiftFound = shiftRepository.findById(info.getIdShift()).orElseThrow( () -> new NotFoundException("Shift not found"));
            gradeToUpdate.setShift(shiftFound);
        }

        if(info.getName() != null){
            gradeToUpdate.setName(info.getName());
        }

        if(info.getSection() != null){
            gradeToUpdate.setSection(info.getSection());
        }

        if(info.getIdGoverment() != null){
            gradeToUpdate.setIdGoverment(info.getIdGoverment());
        }

        Grade gradeFound = gradeRepository.findByNameAndShiftAndSectionOrIdGovermentAndNotId(
            gradeToUpdate.getName(), gradeToUpdate.getShift(), gradeToUpdate.getSection(), gradeToUpdate.getIdGoverment(), gradeToUpdate.getId());

        if (gradeFound != null && !gradeToUpdate.getId().equals(gradeFound.getId())) {
            throw new IllegalArgumentException("Grade already exists");
        }

        gradeFound = gradeRepository.save(gradeToUpdate);
        
        return entityMapper.mapGradeConcatDTO(gradeFound);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void delete(UUID id) throws Exception {
        Grade gradeToDelete = gradeRepository.findById(id).orElseThrow( () -> new NotFoundException("Grade not found"));
        gradeRepository.delete(gradeToDelete);
    }

}
