package com.masferrer.controllers;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.masferrer.models.dtos.AbsenceRecordDTO;
import com.masferrer.models.dtos.AbsenceRecordWithStudentsDTO;
import com.masferrer.models.dtos.CreateAbsentRecordDTO;
import com.masferrer.models.dtos.EditAbsenceRecordDTO;
import com.masferrer.models.dtos.StudentAbsenceCountDTO;
import com.masferrer.models.entities.User;
import com.masferrer.services.AbsenceRecordService;
import com.masferrer.utils.NotFoundException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("api/absence_record")
@CrossOrigin("*")
public class AbsenceRecordController {
    @Autowired
    public AbsenceRecordService absenceRecordService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllAbsenceRecord(){
        List<AbsenceRecordDTO> absenceRecords = absenceRecordService.findAll();
        if(absenceRecords.isEmpty()){
            return new ResponseEntity<>("No absence records found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(absenceRecords, HttpStatus.OK);
    }
    
    @PostMapping("/")
    public ResponseEntity<?> createAbsenceRecord(@RequestBody CreateAbsentRecordDTO info) {
        try {
            AbsenceRecordDTO absenceRecord = absenceRecordService.createAbsenceRecord(info);
            return new ResponseEntity<>(absenceRecord, HttpStatus.OK);
        }catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } 
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/toggle-teacher-active/{id}")
    public ResponseEntity<?> setTeacherValidation(@PathVariable("id") UUID absenceRecordId){
        try {
            boolean teacherUpdated = absenceRecordService.toggleTeacherValidation(absenceRecordId);
            if(!teacherUpdated){
                return new ResponseEntity<>("Absence record not found", HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>("Teacher has validated this absence record", HttpStatus.OK);
            
        } catch (Exception e) {
            return new ResponseEntity<>("Cannot update teacher validation" ,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PatchMapping("/toggle-coordination-active/{id}")
    public ResponseEntity<?> setCoordinationValidation(@PathVariable("id") UUID absenceRecordId){
        try {
            boolean corrdinationUpdated = absenceRecordService.toggleCoordinationValidation(absenceRecordId);
            if(!corrdinationUpdated){
                return new ResponseEntity<>("Absence record not found", HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>("Coordination has validated this absence record", HttpStatus.OK);
            
        } catch (Exception e) {
            return new ResponseEntity<>("Cannot update coordination validation" ,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PatchMapping("/{id}")
    public ResponseEntity<?> editAbsenceRecord(@PathVariable("id") UUID absenceRecordId, @RequestBody EditAbsenceRecordDTO info){
        try {
            AbsenceRecordDTO absenceRecord = absenceRecordService.editAbsenceRecord(info, absenceRecordId);
            return new ResponseEntity<>(absenceRecord, HttpStatus.OK);
        }catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } 
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/get-by-date")
    public ResponseEntity<?> findByDate(@RequestParam("date") LocalDate date){
        List<AbsenceRecordWithStudentsDTO> absenceRecord = absenceRecordService.findByDate(date);
        if(absenceRecord == null){
            return new ResponseEntity<>("Absence record not found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(absenceRecord, HttpStatus.OK);
    }

    @GetMapping("/by-date-noStudent")
    public ResponseEntity<?> findByDateNoStudent(@RequestParam("date") LocalDate date){
        try {
            List<AbsenceRecordDTO> response = absenceRecordService.findByDateNoStudent(date);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred while getting absence records", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
    }

    @GetMapping("/get-by-month")
    public ResponseEntity<List<AbsenceRecordDTO>> getAbsenceRecordsByMonth(@RequestParam("date") String dateStr) {
        LocalDate date = LocalDate.parse(dateStr);
        int month = date.getMonthValue();
        int year = date.getYear();
        List<AbsenceRecordDTO> records = absenceRecordService.findByMonthAndYear(month, year);
        return new ResponseEntity<>(records, HttpStatus.OK);
    }
    @GetMapping("/get-by-classroom/{id}")
    public ResponseEntity<?> getAbsenceRecordsByClassroom(@PathVariable("id") UUID classroomId){
        List<AbsenceRecordDTO> records = absenceRecordService.findByClassroom(classroomId);
        if(records.isEmpty()){
            return new ResponseEntity<>("No absence records found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(records, HttpStatus.OK);
    }
    @GetMapping("/get-by-date-and-classroom")
    public ResponseEntity<?> getAbsenceRecordsByDateAndClassroom(@RequestParam("date") LocalDate date, @RequestParam("id_classroom") UUID classroomId){
        AbsenceRecordDTO record = absenceRecordService.findByDateAndClassroom(date, classroomId);
        if(record == null){
            return new ResponseEntity<>("No absence records found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(record, HttpStatus.OK);
    }
    @GetMapping("/get-by-classroom-shift/{id}")
    public ResponseEntity<?> getAbsenceRecordByClassroomAndShift(@PathVariable("id") UUID idClassroom, @RequestParam("shift") UUID idShift) {
        List<AbsenceRecordWithStudentsDTO> records = absenceRecordService.findByClassroomAndShift(idClassroom, idShift);
        if(records.isEmpty()){
            return new ResponseEntity<>("No absence records found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(records, HttpStatus.OK);
    }

    @GetMapping("/get-by-date-shift")
    public ResponseEntity<?> findByDateAndShift(@RequestParam("date") LocalDate date, @RequestParam("shift") UUID shiftId){
        List<AbsenceRecordDTO> absenceRecord = absenceRecordService.findByDateAndShift(date, shiftId);
        if(absenceRecord == null){
            return new ResponseEntity<>("Absence record not found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(absenceRecord, HttpStatus.OK);
    }

    @GetMapping("/get-by-user-and-date")
    public ResponseEntity<?> getAbsenceRecordsByUserAndDate(@RequestParam("date") LocalDate date) {
        try {
            // obtener al usuario desde el token
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            UUID userId = ((User) userDetails).getId();

            List<AbsenceRecordDTO> records = absenceRecordService.findByUserAndDate(userId, date);
            if (records.isEmpty()) {
                return new ResponseEntity<>("No absence records found", HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(records, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred while getting absence records", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/top-absent-students/{classroomId}")
    public ResponseEntity<?> getTopAbsentStudentsByClassroom(@PathVariable("classroomId") UUID classroomId, @RequestParam("year") String year) {
        List<StudentAbsenceCountDTO> topAbsentStudents = absenceRecordService.getTopAbsentStudentsByClassroom(classroomId, year);
        return new ResponseEntity<>(topAbsentStudents, HttpStatus.OK);
    }

    @GetMapping("/absent-student-count/{classroomId}")
    public ResponseEntity<?> getAllAbsentStudentCount(@PathVariable("classroomId") UUID classroomId, @RequestParam("year") String year) {
        List<StudentAbsenceCountDTO> absentStudents = absenceRecordService.getAbsentStudentsCountByClassroom(classroomId, year);
        return new ResponseEntity<>(absentStudents, HttpStatus.OK);
    }

    @GetMapping("/top-absent-student-by-user")
    public ResponseEntity<?> getTopAbsentStudentsByUserAndShift(@RequestParam("shift") UUID shift, @RequestParam("year") String year) {
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            UUID userId = ((User) userDetails).getId();
            List<StudentAbsenceCountDTO> topAbsentStudents = absenceRecordService.getTopAbsenceStudentsCountByUserAndShift(userId, shift, year);
            return new ResponseEntity<>(topAbsentStudents, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred while getting top absent students", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/all-absent-student-by-user")
    public ResponseEntity<?> getAllAbsentStudentsByUserAndShift(@RequestParam("shift") UUID shift, @RequestParam("year") String year) {
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            UUID userId = ((User) userDetails).getId();
            List<StudentAbsenceCountDTO> topAbsentStudents = absenceRecordService.getAllAbsenceStudentByUserAndShift(userId, shift, year);
            return new ResponseEntity<>(topAbsentStudents, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred while getting top absent students", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    
}
