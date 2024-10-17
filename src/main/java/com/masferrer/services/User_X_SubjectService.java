package com.masferrer.services;

import java.util.List;
import java.util.UUID;

import com.masferrer.models.entities.User_X_Subject;

public interface User_X_SubjectService {
    List<User_X_Subject> findAll();
    Boolean deleteUserxSubject(UUID userXsubjectid);
}
