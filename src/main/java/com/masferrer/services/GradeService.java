package com.masferrer.services;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;

import com.masferrer.models.dtos.SaveGradeDTO;
import com.masferrer.models.dtos.ShowGradeConcatDTO;
import com.masferrer.models.dtos.UpdateGradeDTO;

public interface GradeService {
    List<ShowGradeConcatDTO> findAll();
    Page<ShowGradeConcatDTO> findAll(int page, int size); 
    ShowGradeConcatDTO findById(UUID id);
    List<ShowGradeConcatDTO> findByShift(UUID shiftId);
    ShowGradeConcatDTO save(SaveGradeDTO info) throws Exception;
    ShowGradeConcatDTO update(UUID id, UpdateGradeDTO info) throws Exception;
    void delete(UUID id) throws Exception; 
}
