package com.masferrer.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.masferrer.models.entities.User;

public interface UserRepository extends JpaRepository<User, UUID>{
    User findOneById(UUID id);
    List<User> findByName(String name);
    User findOneByEmailOrName(String name, String email);
    List<User> findByEmailOrName(String name, String email);
    Optional<User> findByEmail(String email);
    List<User> findUsersByRoleId(UUID roleId);
    Page<User> findUsersByRoleId(UUID roleId, Pageable pageable);
    List<User> findByRoleIdAndActive(UUID roleId, Boolean active);
}
