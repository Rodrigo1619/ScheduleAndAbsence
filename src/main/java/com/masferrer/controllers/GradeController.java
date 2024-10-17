package com.masferrer.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.masferrer.models.dtos.PageDTO;
import com.masferrer.models.dtos.SaveGradeDTO;
import com.masferrer.models.dtos.ShowGradeConcatDTO;
import com.masferrer.models.entities.Grade;
import com.masferrer.services.GradeService;
import com.masferrer.utils.EntityMapper;
import com.masferrer.utils.PageMapper;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("api/grade")
@CrossOrigin("*")
public class GradeController {

    @Autowired
    private GradeService gradeService;

    @Autowired
    private PageMapper pageMapper;

    @Autowired
    private EntityMapper entityMapper;

    @GetMapping("/all")
    public ResponseEntity<?> getAllGrades(){
        
        if(gradeService.findAll().isEmpty())
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        
        return new ResponseEntity<>(gradeService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/all-paginated")
    public ResponseEntity<?> getAllGradesPaginated(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size){
        Page<ShowGradeConcatDTO> grades = gradeService.findAll(page, size);

        if(grades.isEmpty())
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        
        PageDTO<ShowGradeConcatDTO> response = pageMapper.map(grades);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getGradeById(@PathVariable("id") String id){
        Grade grade = gradeService.findById(UUID.fromString(id));
        ShowGradeConcatDTO gradeDTO = entityMapper.mapGradeConcatDTO(grade);
        if(gradeDTO == null)
            return new ResponseEntity<>("Grade Not Found", HttpStatus.NOT_FOUND);
        
        return new ResponseEntity<>(gradeDTO, HttpStatus.OK);
    }

    @PostMapping("/")
    public ResponseEntity<?> saveGrade(@Valid @RequestBody SaveGradeDTO info){
        try {
            boolean saved = gradeService.save(info);
            if (!saved) {
                return new ResponseEntity<>("Grade already exists", HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>("Grade created successfully", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateGrade(@PathVariable("id") String id, @Valid @RequestBody SaveGradeDTO info){
        try {
            boolean updated = gradeService.update(info, UUID.fromString(id));
            if (!updated) {
                return new ResponseEntity<>("Error updating grade", HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>("Grade updated successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGrade(@PathVariable("id") String id){
        try {
            boolean deleted = gradeService.delete(UUID.fromString(id));
            if (!deleted) {
                return new ResponseEntity<>("Grade not found", HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>("Grade deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
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
