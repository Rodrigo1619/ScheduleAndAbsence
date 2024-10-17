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
import com.masferrer.models.dtos.SaveCodeDTO;
import com.masferrer.models.dtos.UpdateCodeDTO;
import com.masferrer.models.entities.Code;
import com.masferrer.services.CodeService;
import com.masferrer.utils.PageMapper;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/code")
@CrossOrigin("*")
public class CodeController {

    @Autowired
    private CodeService codeService;

    @Autowired
    private PageMapper pageMapper;

    @GetMapping("/all")
    public ResponseEntity<?> getAllCodes(){
        if(codeService.findAll().isEmpty()){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(codeService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/all-paginated")
    public ResponseEntity<?> getAllCodesPaginated(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size){
        Page<Code> codes = codeService.findAll(page, size);

        if(codes.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        PageDTO<Code> pageDTO = pageMapper.map(codes);
        return new ResponseEntity<>(pageDTO, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCodeById(@PathVariable("id") UUID id){
        Code code = codeService.findById(id);
        if(code == null){
            return new ResponseEntity<>("Code not found",HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(code, HttpStatus.OK);
    }

    @PostMapping("/")
    public ResponseEntity<?> saveCode(@Valid @RequestBody SaveCodeDTO info){
        try {
            if(!codeService.save(info)){
                return new ResponseEntity<>("Code already exists",HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>("Code created successfully",HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateCode(@PathVariable("id") UUID id, @Valid @RequestBody UpdateCodeDTO info){
        try {
            if(!codeService.update(info, id)){
                return new ResponseEntity<>("Error updating code",HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>("Code updated successfully",HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCode(@PathVariable("id") UUID id){
        try {
            if(!codeService.delete(id)){
                return new ResponseEntity<>("Code not found",HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>("Code deleted successfully",HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
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
