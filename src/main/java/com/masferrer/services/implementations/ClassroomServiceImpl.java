package com.masferrer.services.implementations;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.masferrer.models.dtos.FindClassroomDTO;
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
import com.masferrer.utils.NotFoundException;

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

    @Override
    public List<Classroom> findAll() {
        return classroomRepository.findAll();
    }

    @Override
    public Page<Classroom> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return classroomRepository.findAll(pageable);
    }

    @Override
    public Classroom findById(UUID id) {
        return classroomRepository.findById(id).orElse(null);
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

    public List<Classroom> findAllByShiftAndYear(UUID shiftId, String year) {
        if(year == null || year.isEmpty()){
            throw new BadRequestException("year is required");
        }
        Shift shift = shiftRepository.findById(shiftId).orElseThrow( () -> new NotFoundException("Shift not found") );

        return classroomRepository.findByShiftAndYear(shift, year);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public Boolean save(SaveClassroomDTO info, Grade grade, Shift shift, User teacher) {
        Classroom classroomFound = classroomRepository.findByYearAndGradeAndShiftAndUser(info.getYear(), grade, shift, teacher);
    
        if(classroomFound != null) {
            return false;
        }

        Classroom classroom = new Classroom(info.getYear(), grade, shift, teacher);
        classroomRepository.save(classroom);
        return true;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public Boolean update(UpdateClassroomDTO info, UUID id) {
        
        Classroom classroomToUpdate = classroomRepository.findById(id).orElse(null);
        
        if(classroomToUpdate == null) {
            return false;
        }


        if(info.getYear() != null && !(info.getYear().trim().isEmpty())) {
            classroomToUpdate.setYear(info.getYear());
        } else {
            classroomToUpdate.setYear(classroomToUpdate.getYear());
        }

        if (info.getIdGrade() != null) {
            Optional<Grade> gradeOptional = gradeRepository.findById(info.getIdGrade());
            if (gradeOptional.isPresent()) {
                classroomToUpdate.setGrade(gradeOptional.get());
            }
        } else {
            classroomToUpdate.setGrade(classroomToUpdate.getGrade());
        }

        if (info.getIdShift() != null) {
            Optional<Shift> shiftOptional = shiftRepository.findById(info.getIdShift());
            if (shiftOptional.isPresent()) {
                classroomToUpdate.setShift(shiftOptional.get());
            }
        } else {
            classroomToUpdate.setShift(classroomToUpdate.getShift());
        }

        if (info.getIdTeacher() != null) {
            Optional<User> teacherOptional = userRepository.findById(info.getIdTeacher());
            if (teacherOptional.isPresent()) {
                classroomToUpdate.setUser(teacherOptional.get());
            } else {
                return false;
            }
        } else {
            classroomToUpdate.setUser(classroomToUpdate.getUser());
        }

        classroomRepository.save(classroomToUpdate);
        return true;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public Boolean delete(UUID id) {
        Classroom classroomToDelete = classroomRepository.findById(id).orElse(null);
        
        if(classroomToDelete == null) {
            return false;
        }

        classroomRepository.delete(classroomToDelete);
        return true;
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
