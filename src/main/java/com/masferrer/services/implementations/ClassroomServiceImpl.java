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
import com.masferrer.models.dtos.FindClassroomDTO;
import com.masferrer.models.dtos.PageDTO;
import com.masferrer.models.dtos.SaveClassroomDTO;
import com.masferrer.models.dtos.UpdateClassroomDTO;
import com.masferrer.models.entities.Classroom;
import com.masferrer.models.entities.Grade;
import com.masferrer.models.entities.Shift;
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
        Sort sort = Sort.by(
            Sort.Order.asc("year"), 
            Sort.Order.asc("grade.name"), 
            Sort.Order.asc("grade.section"), 
            Sort.Order.asc("shift.name")
        );
        List<Classroom> response = classroomRepository.findAll(sort);
        return entityMapper.mapClassrooms(response);
    }

    @Override
    public PageDTO<CustomClassroomDTO> findAll(int page, int size) {
        Sort sort = Sort.by(
            Sort.Order.asc("year"), 
            Sort.Order.asc("grade.name"), 
            Sort.Order.asc("grade.section"), 
            Sort.Order.asc("shift.name")
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
    public Classroom findByParameters(String year, Grade grade, Shift shift) {
        return classroomRepository.findByYearAndGradeAndShift(year, grade, shift);
    }

    @Override
    public Classroom findByParameters(FindClassroomDTO parameters) {
        Grade grade = gradeRepository.findById(parameters.getIdGrade()).orElseThrow( () -> new NotFoundException("Grade not found") );
        Shift shift = shiftRepository.findById(parameters.getIdShift()).orElseThrow( () -> new NotFoundException("Shift not found") );

        return classroomRepository.findByYearAndGradeAndShift(parameters.getYear(), grade, shift);
    }

    @Override
    public List<Student> findStudentsByClassroom(UUID idClassroom) {        
        Classroom foundClassroom = classroomRepository.findById(idClassroom).orElseThrow( () -> new NotFoundException("Classroom not found") );

        return studentXClassroomRepository.findStudentsByClassroomId(foundClassroom.getId());
    }

    public List<CustomClassroomDTO> findAllByShiftAndYear(UUID shiftId, String year) {
        if(year == null || year.isEmpty()){
            throw new BadRequestException("year is required");
        }
        Shift shift = shiftRepository.findById(shiftId).orElseThrow( () -> new NotFoundException("Shift not found") );

        Sort sort = Sort.by(Sort.Order.asc("grade.name"), Sort.Order.asc("grade.section"));
        List<Classroom> classrooms = classroomRepository.findByShiftAndYear(shift, year, sort);

        return classrooms.stream().map(entityMapper::map).toList();
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public CustomClassroomDTO save(SaveClassroomDTO info) {
        Grade grade = gradeRepository.findById(info.getIdGrade()).orElseThrow( () -> new NotFoundException("Grade not found") );
        Shift shift = shiftRepository.findById(info.getIdShift()).orElseThrow( () -> new NotFoundException("Shift not found") );
        User teacher = userRepository.findById(info.getIdTeacher()).orElseThrow( () -> new NotFoundException("Teacher not found") );
        Classroom classroomFound = classroomRepository.findByYearAndGradeAndShiftAndUser(info.getYear(), grade, shift, teacher);
    
        if(classroomFound != null) {
            throw new IllegalArgumentException("The classroom already exists");
        }

        Classroom classroom = new Classroom(info.getYear(), grade, shift, teacher);
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

        if (info.getIdShift() != null) {
            Shift shift = shiftRepository.findById(info.getIdShift()).orElseThrow( () -> new NotFoundException("Shift not found") );
            classroomToUpdate.setShift(shift);
        } 

        // Check if the classroom data already exists in another classroom
        Classroom existingClassroom = classroomRepository.findByYearAndGradeAndShiftAndNotId(classroomToUpdate.getYear(), classroomToUpdate.getGrade(),classroomToUpdate.getShift(), classroomId);
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
    public List<Classroom> findByUserAndYearAndShift(UUID userId, String year, Shift shift) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return classroomRepository.findByUserAndYearAndShift(user, year, shift);
    }

    @Override
    public List<Classroom> getClassroomsByUser(UUID userId) {
        List<Classroom> classrooms = classroomRepository.findAllByUserId(userId);
        if (classrooms.isEmpty()) {
            throw new NotFoundException("Classrooms not found for the user");
        }
        return classrooms;
    }

    @Override
    public List<Classroom> findByUserAndYear(UUID userId, String year) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return classroomRepository.findByUserAndYear(user, year);
    }
}
