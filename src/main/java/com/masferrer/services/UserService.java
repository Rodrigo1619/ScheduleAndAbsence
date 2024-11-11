package com.masferrer.services;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;

import com.masferrer.models.dtos.AssignSubjectToTeacherDTO;
import com.masferrer.models.dtos.AuthenticationResponse;
import com.masferrer.models.dtos.EditUserDTO;
import com.masferrer.models.dtos.ForgotPasswordDTO;
import com.masferrer.models.dtos.ForgotPasswordResponseDTO;
import com.masferrer.models.dtos.LoginDTO;
import com.masferrer.models.dtos.RegisterDTO;
import com.masferrer.models.dtos.ShortUserDTO;
import com.masferrer.models.dtos.UserDTO;
import com.masferrer.models.dtos.WhoAmIDTO;
import com.masferrer.models.entities.User;

public interface UserService{

    List<User> findAll();
    Page<User> findAll(int page, int size);
    User findById(UUID id);
    UserDTO register(RegisterDTO registerInfo) throws Exception;
    //AuthenticationResponse es un dto que devuelve un token al final
    AuthenticationResponse login(LoginDTO loginInfo);
    List<UserDTO> showUsersAdmin();
    List<ShortUserDTO> findAllUsers();
    Boolean editUser(EditUserDTO userInfo, UUID id) throws Exception;
    Boolean deleteUser(UUID id) throws Exception;
    void changePassword(ForgotPasswordDTO forgotPasswordDTO) throws Exception;
    List<User> findUsersByRoleId(UUID roleId);
    Page<User> findUsersByRoleId(UUID roleId, int page, int size);
    User findByEmail(String email); //para el whoami
    WhoAmIDTO whoAmI();
    void assignSubjectToTeacher(AssignSubjectToTeacherDTO assignSubjectToTeacherDTO);
    List<User> getUsersBySubjectId(UUID subjectId) throws Exception; 
    Boolean toggleActiveStatus(UUID id) throws Exception;
    ForgotPasswordResponseDTO forgotPassword(String email);
    Boolean setPassword(String email, String newPassword);
    String verifyCode(String email, String code);
    List<User> findByRoleIdAndActive(UUID roleId);
    
}

    

