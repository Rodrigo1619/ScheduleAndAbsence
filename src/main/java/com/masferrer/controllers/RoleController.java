package com.masferrer.controllers;


import java.util.List;
import java.util.UUID;

import javax.management.relation.RoleNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.masferrer.models.dtos.PageDTO;
import com.masferrer.models.dtos.ShowRoleDTO;
import com.masferrer.models.entities.Role;
import com.masferrer.services.RoleService;
import com.masferrer.utils.PageMapper;

@RestController
@RequestMapping("api/role")
@CrossOrigin("*")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private PageMapper pageMapper;

    @GetMapping("/all")
    public ResponseEntity<?> getAllRoles(){
        List<Role> roles = roleService.findAll();

        if(roles.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(roles, HttpStatus.OK);
    }

    @GetMapping("/all-paginated")
    public ResponseEntity<?> getAllRolesPaginated(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size){
        Page<Role> roles = roleService.findAll(page, size);

        if(roles.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        PageDTO<Role> response = pageMapper.map(roles);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRoleById(@PathVariable("id") UUID id) throws Exception {
        try {
            ShowRoleDTO role = roleService.showRole(id);
            return new ResponseEntity<>(role, HttpStatus.OK);
        } catch (RoleNotFoundException e) {
            return new ResponseEntity<>("Role not found", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Error Role does not exist", HttpStatus.NOT_FOUND);
        }
    }
    
}
