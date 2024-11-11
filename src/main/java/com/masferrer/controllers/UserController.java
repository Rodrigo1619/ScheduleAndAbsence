package com.masferrer.controllers;


import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.masferrer.models.dtos.AssignSubjectToTeacherDTO;
import com.masferrer.models.dtos.EditUserDTO;
import com.masferrer.models.dtos.PageDTO;
import com.masferrer.models.dtos.ShortUserDTO;
import com.masferrer.models.dtos.UserDTO;
import com.masferrer.models.dtos.WhoAmIDTO;
import com.masferrer.models.entities.User;
import com.masferrer.services.UserService;
import com.masferrer.utils.EntityMapper;
import com.masferrer.utils.NotFoundException;
import com.masferrer.utils.PageMapper;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;



@RestController
@RequestMapping("api/user")
@CrossOrigin("*")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private EntityMapper entityMapper;

    @Autowired
    private PageMapper pageMapper;

    @GetMapping("/all")
    public ResponseEntity<?> getAllUsersAdmin(){
        List<UserDTO> users = userService.showUsersAdmin();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/admin/all-paginated")
    public ResponseEntity<?> getAllUsersAdminPaginated(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size){
        Page<User> users = userService.findAll(page, size);

        if(users.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        List<UserDTO> customList = entityMapper.mapToUserDTO(users.getContent());
        PageDTO<UserDTO> response = pageMapper.map(customList, users);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable("id") UUID id){
        try{
            User user = userService.findById(id);
            UserDTO response = entityMapper.mapUser(user);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch(NotFoundException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> editUser(@PathVariable("id") UUID id, @Valid @RequestBody EditUserDTO userInfo){
        try {
            if(!userService.editUser(userInfo, id)){
                return new ResponseEntity<>("Error: There is a user with the same email", HttpStatus.CONFLICT);
            }
            return new ResponseEntity<>("User updated succesfully", HttpStatus.OK);
        } catch(IllegalArgumentException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch(NotFoundException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error: cannot update user", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") UUID id){
        try{
            if(!userService.deleteUser(id)){
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>("User deleted succesfully", HttpStatus.OK);
        } catch(IllegalArgumentException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch(Exception e){
            return new ResponseEntity<>("Error: User cannot be deleted", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email){ 
        return new ResponseEntity<>(userService.forgotPassword(email), HttpStatus.OK);
    }

    @PutMapping("verify-code")
    public ResponseEntity<?> verifyCode(@RequestParam String email, @RequestParam String code){
        String verifiedEmail = userService.verifyCode(email, code);
    
        if (verifiedEmail != null) {
            return new ResponseEntity<>(verifiedEmail, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Codigo invalido o el codigo expiro", HttpStatus.BAD_REQUEST);
        }
    }
    
    @PutMapping("/set-password")
    public ResponseEntity<?> setPassword(@RequestParam String email, @RequestParam String code ,@RequestHeader String newPassword){
        String verifiedEmail = userService.verifyCode(email, code);

        if (verifiedEmail != null) {
            userService.setPassword(verifiedEmail, newPassword);
            return new ResponseEntity<>("Contraseña actualizada", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Codigo invalido o codigo expirado o codigo ya usado", HttpStatus.BAD_REQUEST);
        }
        
    }
    
    @GetMapping("/role/{roleId}")
    public ResponseEntity<?> getUsersByRoleId(@PathVariable("roleId") UUID roleId) {
        try {
            List<User> users = userService.findByRoleIdAndActive(roleId);
            List<ShortUserDTO> response = entityMapper.mapToShortUserDTO(users);
            if(response.isEmpty()){
                return new ResponseEntity<>("No users found with that role", HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error: cannot get users by role", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    } 
    
    @GetMapping("/role-paginated/{roleId}")
    public ResponseEntity<?> getUsersByRoleIdPaginated(@PathVariable("roleId") UUID roleId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        try {
            Page<User> users = userService.findUsersByRoleId(roleId, page, size);
            List<ShortUserDTO> customList = entityMapper.mapToShortUserDTO(users.getContent());
            if(customList.isEmpty()){
                return new ResponseEntity<>("No users found with that role", HttpStatus.NO_CONTENT);
            }
            PageDTO<ShortUserDTO> response = pageMapper.map(customList, users);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error: cannot get users by role", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    } 

    @GetMapping("/whoami")
    public ResponseEntity<?> whoAmI() {
        try {
            WhoAmIDTO response = userService.whoAmI();
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error: cannot get user info", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/assig-subject")
    public ResponseEntity<?> assignSubjectToTeacher(@RequestBody @Valid AssignSubjectToTeacherDTO assignSubjectToTeacherDTO){
        try {
            userService.assignSubjectToTeacher(assignSubjectToTeacherDTO);
            return new ResponseEntity<>("Subject assigned to teacher successfully", HttpStatus.OK);
        } catch(IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch(Exception e) {
            return new ResponseEntity<>("Error assigning subject to teacher: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<?> getUsersBySubjectId(@PathVariable UUID subjectId) throws Exception{
        try {
            List<User> users = userService.getUsersBySubjectId(subjectId);
            List<ShortUserDTO> response = entityMapper.mapToShortUserDTO(users);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error: cannot get users by subject", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/{id}/toggle-active")
    public ResponseEntity<?> toggleUserActiveStatus(@PathVariable("id") UUID id){
        try {
            boolean updated = userService.toggleActiveStatus(id);
            if(!updated){
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>("User active status updated", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("User's active status cannot be updated", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    //para poder ver el mensaje de error que no existe el id para subjectid
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleSubjectNotFoundException(NotFoundException ex) {
        return ResponseEntity.status(404).body(ex.getMessage());
    }
}