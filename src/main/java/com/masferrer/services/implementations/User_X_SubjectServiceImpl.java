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
import com.masferrer.models.dtos.UserXSubjectDTO;
import com.masferrer.models.entities.User_X_Subject;
import com.masferrer.repository.User_X_SubjectRepository;
import com.masferrer.services.User_X_SubjectService;
import com.masferrer.utils.EntityMapper;
import com.masferrer.utils.PageMapper;

import jakarta.transaction.Transactional;

@Service
public class User_X_SubjectServiceImpl implements User_X_SubjectService {

    @Autowired
    private User_X_SubjectRepository user_X_SubjectRepository;

    @Autowired
    private EntityMapper entityMapper;

    @Autowired
    private PageMapper pageMapper;

    @Override
    public List<UserXSubjectDTO> findAll() {
        Sort sort = Sort.by(
            Sort.Order.asc("subject.name"),
            Sort.Order.asc("user.name")
        );
        List<User_X_Subject> user_X_Subjects = user_X_SubjectRepository.findAll(sort);
        return entityMapper.mapUserXSubjectList(user_X_Subjects);
    }

    @Override
    public PageDTO<UserXSubjectDTO> findAll(int page, int size) {
        Sort sort = Sort.by(
            Sort.Order.asc("subject.name"),
            Sort.Order.asc("user.name")
        );
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<User_X_Subject> resultPage = user_X_SubjectRepository.findAll(pageable);

        List<UserXSubjectDTO> customList = entityMapper.mapUserXSubjectList(resultPage.getContent());
        return pageMapper.map(customList, resultPage);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public Boolean deleteUserxSubject(UUID userXsubjectid) {
        User_X_Subject assignToDelete = user_X_SubjectRepository.findById(userXsubjectid).orElse(null);
        if(assignToDelete == null){
            return false;
        }
        user_X_SubjectRepository.delete(assignToDelete);
        return true;
        
    }

}
