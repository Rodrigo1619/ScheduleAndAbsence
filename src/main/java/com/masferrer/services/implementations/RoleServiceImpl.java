package com.masferrer.services.implementations;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.masferrer.models.dtos.ShowRoleDTO;
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
        Role roleFound = roleRepository.findOneById(id);
        return roleFound;
    }

    @Override
    public List<ShowRoleDTO> showRoles() {
        List<Role> roles = roleRepository.findAll();
        List<ShowRoleDTO> roleDTOS = roles.stream()
        .map(role -> new ShowRoleDTO(role.getId(), role.getName()))
        .collect(Collectors.toList());
        return roleDTOS;
    }

    @Override
    public ShowRoleDTO showRole(UUID id) throws Exception {
        Role role = roleRepository.findById(id).orElse(null);
        if(role == null){
            throw new NotFoundException("Role not found with id " + id);
        }
        ShowRoleDTO showRoleDTO = new ShowRoleDTO(role.getId(), role.getName());
        return showRoleDTO;
    }

    

}
