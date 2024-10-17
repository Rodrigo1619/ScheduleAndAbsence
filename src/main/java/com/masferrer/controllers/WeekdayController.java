package com.masferrer.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.masferrer.models.dtos.ShowWeekdayDTO;
import com.masferrer.services.WeekdayService;

import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("api/weekday")
@CrossOrigin("*")
public class WeekdayController {
    @Autowired
    WeekdayService weekdayService;
    
    @GetMapping("/get-all")
    public ResponseEntity<?> getAllWeekDays(){
        List<ShowWeekdayDTO> weekdays = weekdayService.findAllWeekdays();
        return new ResponseEntity<>(weekdays, HttpStatus.OK);
    }
    
}
