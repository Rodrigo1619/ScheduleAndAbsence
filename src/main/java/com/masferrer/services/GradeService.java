package com.masferrer.services;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;

import com.masferrer.models.dtos.SaveGradeDTO;
import com.masferrer.models.dtos.ShowGradeConcatDTO;
import com.masferrer.models.entities.Grade;

public interface GradeService {
    List<ShowGradeConcatDTO> findAll();
    Page<ShowGradeConcatDTO> findAll(int page, int size); 
    Grade findById(UUID id);
    Boolean save(SaveGradeDTO info) throws Exception;
    Boolean update(SaveGradeDTO info, UUID id) throws Exception;
    Boolean delete(UUID id) throws Exception; 
}
