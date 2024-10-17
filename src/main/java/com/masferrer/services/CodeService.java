package com.masferrer.services;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;

import com.masferrer.models.dtos.SaveCodeDTO;
import com.masferrer.models.dtos.UpdateCodeDTO;
import com.masferrer.models.entities.Code;

public interface CodeService {
    List<Code> findAll();
    Page<Code> findAll(int page, int size);   
    Code findById(UUID id);
    Boolean save(SaveCodeDTO info) throws Exception;
    Boolean update(UpdateCodeDTO info,UUID id) throws Exception;
    Boolean delete(UUID id) throws Exception;
}
