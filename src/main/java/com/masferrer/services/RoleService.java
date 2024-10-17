package com.masferrer.services;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;

import com.masferrer.models.dtos.ShowRoleDTO;
import com.masferrer.models.entities.Role;

public interface RoleService {
    Role findById(UUID id);
    List<Role> findAll();
    Page<Role> findAll(int page, int size); 
    List<ShowRoleDTO> showRoles();
    ShowRoleDTO showRole(UUID id) throws Exception;

}
