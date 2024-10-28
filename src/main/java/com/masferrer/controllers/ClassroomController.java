package com.masferrer.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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

import com.masferrer.models.dtos.AddNewStudentsToClassroomDTO;
import com.masferrer.models.dtos.ClassroomEnrollmentsDTO;
import com.masferrer.models.dtos.ClassroomWithStudentsDTO;
import com.masferrer.models.dtos.CustomClassroomDTO;
import com.masferrer.models.dtos.EnrollStudentsToClassroomDTO;
import com.masferrer.models.dtos.FindClassroomDTO;
import com.masferrer.models.dtos.PageDTO;
import com.masferrer.models.dtos.SaveClassroomDTO;
import com.masferrer.models.dtos.StudentXClassroomDTO;
import com.masferrer.models.dtos.UpdateClassroomDTO;
import com.masferrer.models.entities.Classroom;
import com.masferrer.models.entities.Shift;
import com.masferrer.models.entities.Student;
import com.masferrer.models.entities.User;
import com.masferrer.services.ClassroomService;
import com.masferrer.services.ShiftService;
import com.masferrer.services.StudentXClassroomService;
import com.masferrer.utils.BadRequestException;
import com.masferrer.utils.EntityMapper;
import com.masferrer.utils.NotFoundException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/classroom")
@CrossOrigin("*")
public class ClassroomController {

    @Autowired
    private ClassroomService classroomService;

    @Autowired
    private ShiftService shiftService;

    @Autowired
    private StudentXClassroomService studentXClassroomService;

    @Autowired
    private EntityMapper entityMapper;

    @GetMapping("/all")
    public ResponseEntity<?> getAllClassrooms(){
        try {
            List<CustomClassroomDTO> response = classroomService.findAll();
            if (response.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/all-paginated")
    public ResponseEntity<?> getAllClassroomsPaginated(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size){
        try {
            PageDTO<CustomClassroomDTO> response = classroomService.findAll(page, size);
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

    @GetMapping(value = "/all", params = {"shift", "year"})
    public ResponseEntity<?> getClassroomsByShiftAndYear(@RequestParam(value = "shift") UUID shiftId, @RequestParam(value = "year") String year){ 
        try {
            List<CustomClassroomDTO> response = classroomService.findAllByShiftAndYear(shiftId, year);
            if(response.isEmpty() || response == null){
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (BadRequestException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getClassroomById(@PathVariable("id") UUID id){
        try {
            CustomClassroomDTO response = classroomService.findById(id);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Cambiar a params
    @GetMapping("/by-parameters/{idGrade}/{idShift}/{year}")
    public ResponseEntity<?> getClassroomByParameters(@PathVariable UUID idGrade, @PathVariable UUID idShift, @PathVariable String year){
        FindClassroomDTO info = new FindClassroomDTO(year, idGrade, idShift);
        
        try {
            Classroom classroom = classroomService.findByParameters(info);
            if (classroom == null) {
                return new ResponseEntity<>("Classroom not found",HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(entityMapper.map(classroom), HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } 
    }

    @PostMapping("/")
    public ResponseEntity<?> saveClassroom(@Valid @RequestBody SaveClassroomDTO info){
        try {
            CustomClassroomDTO response = classroomService.save(info);
            return new ResponseEntity<>(response,HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{idClassroom}/students")
    public ResponseEntity<?> getStudentsfromClassroom(@PathVariable UUID idClassroom){
        try {
            List<Student> students = classroomService.findStudentsByClassroom(idClassroom);
            if (students.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(students, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/students")
    public ResponseEntity<?> getStudentsfromClassroomByNie(@RequestParam(value = "nie") String nie, @RequestParam(value = "year") String year){
        try {
            ClassroomWithStudentsDTO response = studentXClassroomService.findStudentsByUserNie(nie, year);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }    

    @GetMapping("/enrollments")
    public ResponseEntity<?> getClassroomEnrollments(@RequestParam(value = "idGrade") UUID gradeId, @RequestParam(value = "idShift") UUID shiftId , @RequestParam(value = "year") String year){
        try {
            List<ClassroomEnrollmentsDTO> response = studentXClassroomService.findEnrollmentsByClassroom(gradeId, shiftId, year);
            if (response.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/students")
    public ResponseEntity<?> AddNewStudentsToClassroom(@RequestBody @Valid AddNewStudentsToClassroomDTO info){
        try {
            List<StudentXClassroomDTO> response = studentXClassroomService.addStudentsToClassroom(info);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (BadRequestException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/students")
    public ResponseEntity<?> ChangeStudentsToOtherClassroom(@RequestBody @Valid EnrollStudentsToClassroomDTO info){
        try {
            List<StudentXClassroomDTO> response = studentXClassroomService.changeStudentsToOtherClassroom(info);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (BadRequestException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateClassroom(@RequestBody UpdateClassroomDTO info, @PathVariable("id") UUID id){
        try {
            CustomClassroomDTO response = classroomService.update(info, id);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (BadRequestException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteClassroom(@PathVariable("id") UUID id){
        try {
            classroomService.delete(id);
            return new ResponseEntity<>("Classroom deleted", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //obtener classroom por el usuario autenticado en el token y el año
    @GetMapping("/by-user-and-year-and-shift")
    public ResponseEntity<?> getClassroomsByUserAndYearAndShift(@RequestParam("year") String year, @RequestParam("shift") UUID shiftId){
        // obtener al usuario desde el token
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UUID userId = ((User) userDetails).getId();
        Shift shift = shiftService.findById(shiftId);
        List<Classroom> classrooms = classroomService.findByUserAndYearAndShift(userId, year, shift);
        if(classrooms.isEmpty()){
            return new ResponseEntity<>("No classrooms found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(entityMapper.mapClassrooms(classrooms), HttpStatus.OK);
    }

    @GetMapping("/by-user-and-year")
    public ResponseEntity<?> getClassroomsByUserAndYear(@RequestParam("year") String year){
        // obtener al usuario desde el token
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UUID userId = ((User) userDetails).getId();
        List<Classroom> classrooms = classroomService.findByUserAndYear(userId, year);
        if(classrooms.isEmpty()){
            return new ResponseEntity<>("No classrooms found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(entityMapper.mapClassrooms(classrooms), HttpStatus.OK);
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
