package com.masferrer.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.masferrer.models.dtos.PageDTO;
import com.masferrer.models.dtos.SaveSubjectDTO;
import com.masferrer.models.entities.Subject;
import com.masferrer.services.SubjectService;
import com.masferrer.utils.NotFoundException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/subject")
@CrossOrigin("*")
public class SubjectController {

    @Autowired
    private SubjectService subjectService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllSubjects() {
        List<Subject> subjects = subjectService.findAll();
        
        if(subjects.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); 
        }
        return new ResponseEntity<>(subjects, HttpStatus.OK);
    }

    @GetMapping("/all-paginated")
    public ResponseEntity<?> getAllSubjectsPaginated(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        PageDTO<Subject> response = subjectService.findAll(page, size);

        if(response.getContent().isEmpty()) {
           return new ResponseEntity<>(HttpStatus.NO_CONTENT); 
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSubjectById(@PathVariable("id") UUID id) {
        Subject subject = subjectService.findById(id);
        
        if(subject == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(subject, HttpStatus.OK);
    }

    @PostMapping("/")
    public ResponseEntity<?> saveSubject(@Valid @RequestBody SaveSubjectDTO info) {
        try {
            if (!subjectService.save(info)) {
                return new ResponseEntity<>("Subject already exists", HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>("Subject saved",HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error saving student",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateSubject(@Valid @RequestBody SaveSubjectDTO info, @PathVariable("id") UUID id) {
        try {
            if (!subjectService.update(info, id)) {
                return new ResponseEntity<>("Error updating subject", HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>("Subject updated", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error updating subject", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSubject(@PathVariable("id") UUID id) {
        try {
            if (!subjectService.delete(id)) {
                return new ResponseEntity<>("Subject not found", HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>("Subject deleted", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error deleting subject", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/get-subject-by-userId/{userId}")
    public ResponseEntity<?> getSubjectsByUserId(@PathVariable UUID userId) throws Exception{
        try {
            List<Subject> subjects = subjectService.getSubjectsByUserId(userId);
            return new ResponseEntity<>(subjects, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
    MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}
