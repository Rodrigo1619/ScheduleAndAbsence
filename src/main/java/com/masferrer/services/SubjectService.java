package com.masferrer.services;

import java.util.List;
import java.util.UUID;

import com.masferrer.models.dtos.PageDTO;
import com.masferrer.models.dtos.SaveSubjectDTO;
import com.masferrer.models.entities.Subject;

public interface SubjectService {
    List<Subject> findAll();
    PageDTO<Subject> findAll(int page, int size); 
    Subject findById(UUID id);
    Boolean save (SaveSubjectDTO info) throws Exception;
    Boolean update(SaveSubjectDTO info, UUID id) throws Exception;
    Boolean delete(UUID id) throws Exception;
    List<Subject> getSubjectsByUserId(UUID userId) throws Exception;
}
