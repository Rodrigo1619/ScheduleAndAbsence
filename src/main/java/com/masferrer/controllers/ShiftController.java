package com.masferrer.controllers;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.masferrer.models.entities.Shift;
import com.masferrer.services.ShiftService;

@RestController
@RequestMapping("api/shift")
@CrossOrigin("*")
public class ShiftController {

    @Autowired
    private ShiftService shiftService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllShifts() {

        if(shiftService.findAll().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(shiftService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getShiftById(@PathVariable("id") String id) {
        Shift shift = shiftService.findById(UUID.fromString(id));  

        if(shift == null) {
            return new ResponseEntity<>("Shift not found",HttpStatus.NOT_FOUND);
        } 
        return new ResponseEntity<>(shift, HttpStatus.OK);
    }
}
