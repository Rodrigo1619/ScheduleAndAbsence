package com.masferrer.services.implementations;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.masferrer.models.dtos.PageDTO;
import com.masferrer.models.dtos.SaveSubjectDTO;
import com.masferrer.models.entities.Subject;
import com.masferrer.models.entities.User;
import com.masferrer.repository.SubjectRepository;
import com.masferrer.repository.UserRepository;
import com.masferrer.repository.User_X_SubjectRepository;
import com.masferrer.services.SubjectService;
import com.masferrer.utils.NotFoundException;
import com.masferrer.utils.PageMapper;

import jakarta.transaction.Transactional;

@Service
public class SubjectServiceImpl implements SubjectService {

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private User_X_SubjectRepository user_X_SubjectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PageMapper pageMapper;

    @Override
    public List<Subject> findAll() {
        Sort sort = Sort.by(Sort.Direction.ASC, "name");
        return subjectRepository.findAll(sort);
    }

    public PageDTO<Subject> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Subject> resulPage =  subjectRepository.findAll(pageable);

        return pageMapper.map(resulPage);
    }

    @Override
    public Subject findById(UUID id) {
        return subjectRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public Boolean save(SaveSubjectDTO info) {
        Subject subjectFound = subjectRepository.findByName(info.getName());

        if (subjectFound != null) {
            return false;
        }

        Subject subject = new Subject(info.getName());
        subjectRepository.save(subject);
        return true;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public Boolean update(SaveSubjectDTO info, UUID id) {
        Subject subjectToUpdate = subjectRepository.findById(id).orElse(null);

        if (subjectToUpdate == null) {
            return false;
        }

        // Check if the name is already used by another subject
        Subject existingSubject = subjectRepository.findByName(info.getName());
        if (existingSubject != null) {
            // The name is already used by another subject
            return false;
        }

        subjectToUpdate.setName(info.getName());
        subjectRepository.save(subjectToUpdate);

        return true;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public Boolean delete(UUID id) {
        Subject subjectToDelete = subjectRepository.findById(id).orElse(null);

        if (subjectToDelete == null) {
            return false;
        }

        subjectRepository.delete(subjectToDelete);
        return true;
    }

    @Override
    public List<Subject> getSubjectsByUserId(UUID userId) throws Exception {
        //encontrando al usuario
        User user = userRepository.findById(userId).orElse(null);
        if(user ==null){
            throw new NotFoundException("User not found with ID: " + userId);
        }
        return user_X_SubjectRepository.findSubjectsByUserId(userId);

    }
}
