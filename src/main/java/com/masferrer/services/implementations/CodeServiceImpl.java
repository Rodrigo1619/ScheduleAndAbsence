package com.masferrer.services.implementations;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.masferrer.models.dtos.SaveCodeDTO;
import com.masferrer.models.dtos.UpdateCodeDTO;
import com.masferrer.models.entities.Code;
import com.masferrer.repository.CodeRepository;
import com.masferrer.services.CodeService;

import jakarta.transaction.Transactional;

@Service
public class CodeServiceImpl implements CodeService{

    @Autowired
    private CodeRepository codeRepository;

    @Override
    public List<Code> findAll() {
        return codeRepository.findAll();
    }

    @Override
    public Page<Code> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return codeRepository.findAll(pageable);
    }

    @Override
    public Code findById(UUID id) {
        return codeRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public Boolean save(SaveCodeDTO info) {
        Code codeFound = codeRepository.findByDescription(info.getDescription());

        if(codeFound != null){
            return false;
        }

        Code code = new Code(info.getDescription());
        codeRepository.save(code);
        return true;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public Boolean update(UpdateCodeDTO info, UUID id) {
        Code codeToUpdate = codeRepository.findById(id).orElse(null);

        if(codeToUpdate == null){
            return false;
        }

        Code codeFound = codeRepository.findByDescription(info.getDescription());
        if(codeFound != null && !codeFound.getId().equals(id)){
            return false;
        }
        codeToUpdate.setDescription(info.getDescription() != null && !(info.getDescription().trim().isEmpty())? info.getDescription() : codeToUpdate.getDescription());
        codeRepository.save(codeToUpdate);

        return true;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public Boolean delete(UUID id) {
        Code codeToDelete = codeRepository.findById(id).orElse(null);

        if(codeToDelete == null){
            return false;
        }

        codeRepository.delete(codeToDelete);
        return true;
    }

}
