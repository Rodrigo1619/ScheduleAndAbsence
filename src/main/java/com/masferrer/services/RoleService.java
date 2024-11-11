package com.masferrer.services;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;

import com.masferrer.models.entities.Role;

public interface RoleService {
    Role findById(UUID id);
    List<Role> findAll();
    Page<Role> findAll(int page, int size); 
}
