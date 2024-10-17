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

import com.masferrer.configs.JwtService;
import com.masferrer.models.dtos.AssignSubjectToTeacherDTO;
import com.masferrer.models.dtos.EditUserDTO;
import com.masferrer.models.dtos.PageDTO;
import com.masferrer.models.dtos.ShortUserDTO;
import com.masferrer.models.dtos.UserDTO;
import com.masferrer.models.dtos.WhoAmIDTO;
import com.masferrer.models.entities.Role;
import com.masferrer.models.entities.User;
import com.masferrer.repository.RoleRepository;
import com.masferrer.services.UserService;
import com.masferrer.utils.EntityMapper;
import com.masferrer.utils.NotFoundException;
import com.masferrer.utils.PageMapper;

import jakarta.validation.Valid;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;



@RestController
@RequestMapping("api/user")
@CrossOrigin("*")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private EntityMapper entityMapper;

    @Autowired
    private PageMapper pageMapper;


    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers(){
        List<ShortUserDTO> users = userService.findAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/admin/all")
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

    @PatchMapping("/edit/{id}")
    public ResponseEntity<?> editUser(@PathVariable("id") UUID id, @Valid @RequestBody EditUserDTO userInfo){
        try {
            if(!userService.editUser(userInfo, id)){
                return new ResponseEntity<>("Error: cannot update user", HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>("User updated succesfully", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error: cannot update user, talk to admin", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") UUID id){
        try{
            if(!userService.deleteUser(id)){
                return new ResponseEntity<>("Error: User cannot be deleted", HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>("User deleted succesfully", HttpStatus.OK);
        }catch(Exception e){
            e.printStackTrace();
            return new ResponseEntity<>("Error: User cannot be deleted, talk to admin", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /* @PutMapping("/forgot-password")
    public ResponseEntity<?> changePassword(@RequestBody @Valid ForgotPasswordDTO forgotPasswordDTO) {
        try {
            userService.changePassword(forgotPasswordDTO);
            return new ResponseEntity<>("Password changed successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error changing password: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    } */
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
            return new ResponseEntity<>("Invalid code or code is expired", HttpStatus.BAD_REQUEST);
        }
    }
    

    @PutMapping("/set-password")
    public ResponseEntity<?> setPassword(@RequestParam String email, @RequestParam String code ,@RequestHeader String newPassword){
        String verifiedEmail = userService.verifyCode(email, code);

        if (verifiedEmail != null) {
            userService.setPassword(verifiedEmail, newPassword);
            return new ResponseEntity<>("Password updated", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Invalid code or code expired or code has been already used", HttpStatus.BAD_REQUEST);
        }
        
    }
    
    @GetMapping("/role/{roleId}")
    public ResponseEntity<?> getUsersByRoleId(@PathVariable("roleId") UUID roleId) {
        // ver si el role existe
        Role role = roleRepository.findOneById(roleId);
        if (role == null) {
            return new ResponseEntity<>("Error: Role with ID " + roleId + " does not exist", HttpStatus.BAD_REQUEST);
        }
    
        List<User> users = userService.findUsersByRoleId(roleId);
        List<ShortUserDTO> response = entityMapper.mapToShortUserDTO(users);
        return new ResponseEntity<>(response, HttpStatus.OK);
    } 
    
    @GetMapping("/role-paginated/{roleId}")
    public ResponseEntity<?> getUsersByRoleIdPaginated(@PathVariable("roleId") UUID roleId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        // ver si el role existe
        Role role = roleRepository.findOneById(roleId);
        if (role == null) {
            return new ResponseEntity<>("Error: Role with ID " + roleId + " does not exist", HttpStatus.BAD_REQUEST);
        }
    
        Page<User> users = userService.findUsersByRoleId(roleId, page, size);
        List<ShortUserDTO> customList = entityMapper.mapToShortUserDTO(users.getContent());
        PageDTO<ShortUserDTO> response = pageMapper.map(customList, users);
        return new ResponseEntity<>(response, HttpStatus.OK);
    } 

    @GetMapping("/whoami")
    public ResponseEntity<?> whoAmI() {
        /* Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
            return new ResponseEntity<>("Error: Invalid or expired token", HttpStatus.UNAUTHORIZED);
        } */
        WhoAmIDTO whoAmIDTO = userService.whoAmIDTO();
        return new ResponseEntity<>(whoAmIDTO, HttpStatus.OK);
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
        List<User> users = userService.getUsersBySubjectId(subjectId);
        List<ShortUserDTO> response = entityMapper.mapToShortUserDTO(users);
        return new ResponseEntity<>(response, HttpStatus.OK);
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




