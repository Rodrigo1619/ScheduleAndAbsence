package com.masferrer.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import org.springframework.web.bind.annotation.RestController;


import com.masferrer.models.dtos.CreateScheduleListDTO;
import com.masferrer.models.dtos.ScheduleListDTO;
import com.masferrer.models.dtos.UpdateScheduleDTO;
import com.masferrer.models.entities.Schedule;
import com.masferrer.services.ScheduleService;
import com.masferrer.utils.BadRequestException;
import com.masferrer.utils.ExistExceptions;
import com.masferrer.utils.NotFoundException;

import jakarta.validation.Valid;



@RestController
@RequestMapping("api/schedule")
@CrossOrigin("*")
public class ScheduleController {
    @Autowired
    private ScheduleService scheduleService;

    @PostMapping("/")
    public ResponseEntity<?> createSchedules(@RequestBody @Valid CreateScheduleListDTO createScheduleListDTO) {
        try {
            List<ScheduleListDTO> schedules = scheduleService.createSchedule(createScheduleListDTO);
            return new ResponseEntity<>(schedules, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (ExistExceptions e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PatchMapping("/")
    public ResponseEntity<?> updateSchedule(@RequestBody List<UpdateScheduleDTO> listSchedules) {
        try {
            List<Schedule> schedules = scheduleService.updateSchedule(listSchedules);
            return new ResponseEntity<>(schedules, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred while updating schedules", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/user/{userId}/{year}")
    public ResponseEntity<?> getSchedulesByUserIdAndYear(@PathVariable UUID userId, @PathVariable int year) {
        List<ScheduleListDTO> schedules = scheduleService.getSchedulesByUserIdAndYear(userId, year);

        if (schedules.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(schedules, HttpStatus.OK);
    }

    @GetMapping("/user")
    public ResponseEntity<?> getScheduleByTokenAndShiftAndYear(@RequestParam("shift") UUID shiftId, @RequestParam String year) {
        try {
            List<ScheduleListDTO> schedules = scheduleService.getSchedulesByUserTokenAndShiftAndYear(shiftId,year);

            if (schedules.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(schedules, HttpStatus.OK);
        } catch (BadRequestException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred while getting schedules", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/classroom/{classroomId}")
    public ResponseEntity<?> getScheduleByClassroomId(@PathVariable UUID classroomId){
        List<ScheduleListDTO> schedules = scheduleService.getScheduleByClassroomId(classroomId);
        return new ResponseEntity<>(schedules, HttpStatus.OK);
    }

    @DeleteMapping("/")
    public ResponseEntity<?> deleteSchedule(@RequestBody List<UUID> schedulesIds) {
        try {
            scheduleService.deleteSchedule(schedulesIds);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred while deleting schedules", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllSchedules(){
        List<ScheduleListDTO> schedules = scheduleService.findAll();
        if(schedules.isEmpty()){
            return new ResponseEntity<>("no schedules found", HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(schedules, HttpStatus.OK);
    }

    @ExceptionHandler(ExistExceptions.class)
    public ResponseEntity<String> handleOverlappingScheduleException(ExistExceptions ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
