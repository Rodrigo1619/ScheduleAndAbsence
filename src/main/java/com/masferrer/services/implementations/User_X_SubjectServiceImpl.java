package com.masferrer.services.implementations;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.masferrer.models.entities.User_X_Subject;
import com.masferrer.repository.User_X_SubjectRepository;
import com.masferrer.services.User_X_SubjectService;

import jakarta.transaction.Transactional;

@Service
public class User_X_SubjectServiceImpl implements User_X_SubjectService {

    @Autowired
    User_X_SubjectRepository user_X_SubjectRepository;

    @Override
    public List<User_X_Subject> findAll() {
        List<User_X_Subject> user_X_Subjects = user_X_SubjectRepository.findAll();
        return user_X_Subjects;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public Boolean deleteUserxSubject(UUID userXsubjectid) {
        //encontrando el id de la asignacion (es mas facil hacerlo asi que con id user y subject por separado)
        User_X_Subject assignToDelete = user_X_SubjectRepository.findById(userXsubjectid).orElse(null);
        if(assignToDelete == null){
            return false;
        }
        user_X_SubjectRepository.delete(assignToDelete);
        return true;
        
    }

}
