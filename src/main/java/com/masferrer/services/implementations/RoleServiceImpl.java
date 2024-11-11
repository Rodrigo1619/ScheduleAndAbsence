package com.masferrer.services.implementations;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.masferrer.models.entities.Role;
import com.masferrer.repository.RoleRepository;
import com.masferrer.services.RoleService;
import com.masferrer.utils.NotFoundException;

@Service
public class RoleServiceImpl implements RoleService {
    @Autowired
    RoleRepository roleRepository;

    @Override
    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    @Override
    public Page<Role> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return roleRepository.findAll(pageable);
    }

    @Override
    public Role findById(UUID id) {
        Role roleFound = roleRepository.findById(id).orElseThrow(() -> new NotFoundException("Role not found"));
        return roleFound;
    }
}
