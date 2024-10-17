package com.masferrer.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.masferrer.models.entities.ClassPeriod;
import com.masferrer.services.ClassPeriodService;
import com.masferrer.utils.NotFoundException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("api/class-period")
@CrossOrigin("*")
public class ClassPeriodController {

    @Autowired
    private ClassPeriodService classPeriodService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllClassPeriods() {
        List<ClassPeriod> response = classPeriodService.findAll();

        if(response.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getClassPeriodById(@PathVariable("id") UUID id) {
        try {
            ClassPeriod response = classPeriodService.findById(id);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
}
