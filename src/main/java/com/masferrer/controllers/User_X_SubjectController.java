package com.masferrer.controllers;


import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.masferrer.models.dtos.PageDTO;
import com.masferrer.models.dtos.UserXSubjectDTO;
import com.masferrer.services.User_X_SubjectService;

@RestController
@RequestMapping("api/user_x_subject")
@CrossOrigin("*")
public class User_X_SubjectController {
    @Autowired
    private User_X_SubjectService user_X_SubjectService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllUser_X_Subject(){
        List<UserXSubjectDTO> user_x_subject = user_X_SubjectService.findAll();

        if(user_x_subject.isEmpty()){
            return new ResponseEntity<>("No subjects assgined to teachers", HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(user_x_subject, HttpStatus.OK);
    }

    @GetMapping("/all-paginated")
    public ResponseEntity<?> getAllClassroomsPaginated(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size){
        try {
            PageDTO<UserXSubjectDTO> response = user_X_SubjectService.findAll(page, size);
            if (response.getContent().isEmpty() || response == null) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser_X_Subject(@PathVariable("id") UUID id){
        try {
            Boolean deleted = user_X_SubjectService.deleteUserxSubject(id);
            if(!deleted){
                return new ResponseEntity<>("Assign cannot be deleted", HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>("Assign with id: " + id + " deleted succesfully" ,HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Oh no internal error talk to admin", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
