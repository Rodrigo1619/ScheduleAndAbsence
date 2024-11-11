package com.masferrer.controllers;

import java.util.HashMap;
import java.util.List;
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

import com.masferrer.models.dtos.EnrollStudentsToClassroomDTO;
import com.masferrer.models.dtos.PageDTO;
import com.masferrer.models.dtos.SaveStudentDTO;
import com.masferrer.models.dtos.StudentXClassroomDTO;
import com.masferrer.models.dtos.UpdateStudentDTO;
import com.masferrer.models.entities.Student;
import com.masferrer.services.StudentService;
import com.masferrer.services.StudentXClassroomService;
import com.masferrer.utils.BadRequestException;
import com.masferrer.utils.NotFoundException;
import com.masferrer.utils.PageMapper;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/student")
@CrossOrigin("*")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private StudentXClassroomService studentXClassroomService;

    @Autowired
    private PageMapper pageMapper;

    @GetMapping("/all")
    public ResponseEntity<?> getAllStudents() {
        List<Student> students = studentService.findAll();

        if(students.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); 
        }
        return new ResponseEntity<>(students, HttpStatus.OK);
    }
    
    @GetMapping("/all-paginated")
    public ResponseEntity<?> getAllStudentsPaginated(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Page<Student> students = studentService.findAll(page, size);

        if(students.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); 
        }

        PageDTO<Student> response = pageMapper.map(students);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/new")
    public ResponseEntity<?> getNewStudents() {
        try {
            List<Student> students = studentService.findNewStudents();

            if(students.isEmpty()){
                return new ResponseEntity<>(HttpStatus.NO_CONTENT); 
            }
            return new ResponseEntity<>(students, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/enroll")
    public ResponseEntity<?> EnrollStudentsToNextYearClassroom(@RequestBody @Valid EnrollStudentsToClassroomDTO info){
        try {
            List<StudentXClassroomDTO> response = studentXClassroomService.enrollStudentsToClassroom(info);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (BadRequestException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getStudentById(@PathVariable("id") UUID id){
        Student student = studentService.findById(id);

        if(student == null){
            return new ResponseEntity<>("Student not found",HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(student, HttpStatus.OK);
    }

    @PostMapping("/")
    public ResponseEntity<?> saveStudent(@Valid @RequestBody SaveStudentDTO info){
        try {
            Student response = studentService.save(info);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Error saving student", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
    }

    @PatchMapping("/{id}/toggle-active")
    public ResponseEntity<?> toggleStudentActiveStatus(@PathVariable("id") UUID id) {
        try {
            boolean updated = studentService.toggleActiveStatus(id);
            if (!updated) {
                return new ResponseEntity<>("Student not found", HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>("Student active status updated", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Error toggling student's active status", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateStudent(@PathVariable("id") UUID id, @Valid @RequestBody UpdateStudentDTO info){
        try {
            Student response = studentService.update(info, id);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error updating student", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStudent(@PathVariable("id") UUID id){
        try {
            if(!studentService.delete(id)){
                return new ResponseEntity<>("Student not found", HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>("Student deleted", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Error deleting student", HttpStatus.INTERNAL_SERVER_ERROR);
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
