package com.masferrer.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.masferrer.models.entities.Role;

public interface RoleRepository extends JpaRepository<Role, UUID>{
    Role findByName(String roleName); //si no sirve cambiar por findByRole
}
