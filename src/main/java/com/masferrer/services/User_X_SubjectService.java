package com.masferrer.services;

import java.util.List;
import java.util.UUID;

import com.masferrer.models.dtos.PageDTO;
import com.masferrer.models.dtos.UserXSubjectDTO;

public interface User_X_SubjectService {
    List<UserXSubjectDTO> findAll();
    PageDTO<UserXSubjectDTO> findAll(int page, int size);
    Boolean deleteUserxSubject(UUID userXsubjectid);
}
