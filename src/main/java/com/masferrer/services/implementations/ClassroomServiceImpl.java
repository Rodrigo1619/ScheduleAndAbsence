package com.masferrer.services.implementations;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.masferrer.models.dtos.CustomClassroomDTO;
import com.masferrer.models.dtos.PageDTO;
import com.masferrer.models.dtos.SaveClassroomDTO;
import com.masferrer.models.dtos.UpdateClassroomDTO;
import com.masferrer.models.entities.Classroom;
import com.masferrer.models.entities.Grade;
import com.masferrer.models.entities.Student;
import com.masferrer.models.entities.User;
import com.masferrer.repository.ClassroomRepository;
import com.masferrer.repository.GradeRepository;
import com.masferrer.repository.ShiftRepository;
import com.masferrer.repository.StudentXClassroomRepository;
import com.masferrer.repository.UserRepository;
import com.masferrer.services.ClassroomService;
import com.masferrer.utils.BadRequestException;
import com.masferrer.utils.EntityMapper;
import com.masferrer.utils.NotFoundException;
import com.masferrer.utils.PageMapper;

import jakarta.transaction.Transactional;

@Service
public class ClassroomServiceImpl implements ClassroomService{

    @Autowired
    private ClassroomRepository classroomRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private ShiftRepository shiftRepository;

    @Autowired
    private StudentXClassroomRepository studentXClassroomRepository;

    @Autowired
    private EntityMapper entityMapper;

    @Autowired
    private PageMapper pageMapper;

    @Override
    public List<CustomClassroomDTO> findAll() {
        List<Classroom> result = classroomRepository.findAllSorted();
        return entityMapper.mapClassrooms(result);
    }

    @Override
    public PageDTO<CustomClassroomDTO> findAll(int page, int size) {
        Sort sort = Sort.by(
            Sort.Order.desc("year"), 
            Sort.Order.asc("grade.name"), 
            Sort.Order.asc("grade.section"), 
            Sort.Order.asc("grade.shift.name")
        );
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Classroom> resultPage = classroomRepository.findAll(pageable);
        List<CustomClassroomDTO> customList = entityMapper.mapClassrooms(resultPage.getContent());

        return pageMapper.map(customList, resultPage);
    }

    @Override
    public CustomClassroomDTO findById(UUID id) {
        Classroom result = classroomRepository.findById(id).orElseThrow( () -> new NotFoundException("Classroom not found") );

        return entityMapper.map(result);
    }

    @Override
    public CustomClassroomDTO findByParameters(UUID idGrade, String year) {
        if(!gradeRepository.existsById(idGrade)) {
            throw new NotFoundException("Grade not found");
        }

        Classroom result = classroomRepository.findByYearAndGrade(year, idGrade);
        if (result == null) {
            throw new NotFoundException("Classroom not found");
        }
        return entityMapper.map(result);
    }

    @Override
    public List<Student> findStudentsByClassroom(UUID idClassroom) {        
        if(!classroomRepository.existsById(idClassroom)) {
            throw new NotFoundException("Classroom not found");
        }

        return studentXClassroomRepository.findStudentsByClassroomId(idClassroom);
    }

    public List<CustomClassroomDTO> findAllByShiftAndYear(UUID shiftId, String year) {
        if(year == null || year.isEmpty()){
            throw new IllegalArgumentException("year is required");
        }
        if(!shiftRepository.existsById(shiftId)) {
            throw new NotFoundException("Shift not found");
        }

        List<Classroom> classrooms = classroomRepository.findByShiftAndYear(shiftId, year);
        return classrooms.stream().map(entityMapper::map).toList();
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public CustomClassroomDTO save(SaveClassroomDTO info) {
        Grade grade = gradeRepository.findById(info.getIdGrade()).orElseThrow( () -> new NotFoundException("Grade not found") );
        User teacher = userRepository.findById(info.getIdTeacher()).orElseThrow( () -> new NotFoundException("Teacher not found") );

        Classroom classroomFound = classroomRepository.findByYearAndGrade(info.getYear(), grade.getId());
    
        if(classroomFound != null) {
            throw new IllegalArgumentException("The classroom already exists");
        }

        Classroom classroom = new Classroom(info.getYear(), grade, teacher);
        classroom = classroomRepository.save(classroom);
        return entityMapper.map(classroom);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public CustomClassroomDTO update(UpdateClassroomDTO info, UUID classroomId) {
        
        Classroom classroomToUpdate = classroomRepository.findById(classroomId).orElseThrow( () -> new NotFoundException("Classroom not found") );

        if(info.getYear() != null && !(info.getYear().trim().isEmpty())) {
            classroomToUpdate.setYear(info.getYear());
        }

        if (info.getIdGrade() != null) {
            Grade grade = gradeRepository.findById(info.getIdGrade()).orElseThrow( () -> new NotFoundException("Grade not found") );
            classroomToUpdate.setGrade(grade);  
        } 

        // Check if the classroom data already exists in another classroom
        Classroom existingClassroom = classroomRepository.findByYearAndGradeAndNotId(classroomToUpdate.getYear(), classroomToUpdate.getGrade().getId(), classroomId);
        if (existingClassroom != null) {
            throw new BadRequestException("The classroom already exists");
        }

        if (info.getIdTeacher() != null) {
            User teacher = userRepository.findById(info.getIdTeacher()).orElseThrow( () -> new NotFoundException("Teacher not found") );
            classroomToUpdate.setUser(teacher);
        } 

        Classroom response = classroomRepository.save(classroomToUpdate);
        return entityMapper.map(response);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void delete(UUID id) {
        Classroom classroomToDelete = classroomRepository.findById(id).orElseThrow( () -> new NotFoundException("Classroom not found") );
        classroomRepository.delete(classroomToDelete);
    }

    @Override
    public List<CustomClassroomDTO> findByUserAndYearAndShift(String year, UUID shiftId) {
        if(year == null || year.isEmpty()){
            throw new IllegalArgumentException("year is required");
        }
        if(shiftId == null || shiftId.toString().isEmpty()){
            throw new IllegalArgumentException("shiftId is required");
        }

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if(!shiftRepository.existsById(shiftId)) {
            throw new NotFoundException("Shift not found");
        }
        List<Classroom> result = classroomRepository.findByUserAndYearAndShift(user.getId(), year, shiftId);
        if (result.isEmpty()) {
            throw new NotFoundException("No classrooms assigned to the user");
        }

        return entityMapper.mapClassrooms(result);
    }

    @Override
    public List<CustomClassroomDTO> findByUserAndYear(String year) {
        if(year == null || year.isEmpty()){
            throw new IllegalArgumentException("year is required");
        }

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<Classroom> result = classroomRepository.findByUserAndYear(user.getId(), year);
        if (result.isEmpty()) {
            throw new NotFoundException("No classrooms assigned to the user");
        }

        return entityMapper.mapClassrooms(result);
    }

    @Override
    public List<Classroom> getClassroomsByUser(UUID userId) {
        Sort sort = Sort.by(
            Sort.Order.asc("year"), 
            Sort.Order.asc("grade.name"), 
            Sort.Order.asc("grade.section"), 
            Sort.Order.asc("grade.shift.name")
        );

        List<Classroom> classrooms = classroomRepository.findAllByUserId(userId, sort);
        if (classrooms.isEmpty()) {
            throw new NotFoundException("Classrooms not found for the user");
        }
        
        return classrooms;
    }
}
