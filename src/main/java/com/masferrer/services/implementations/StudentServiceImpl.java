package com.masferrer.services.implementations;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.masferrer.models.dtos.FindClassroomDTO;
import com.masferrer.models.dtos.SaveStudentDTO;
import com.masferrer.models.dtos.UpdateStudentDTO;
import com.masferrer.models.entities.Classroom;
import com.masferrer.models.entities.Grade;
import com.masferrer.models.entities.Shift;
import com.masferrer.models.entities.Student;
import com.masferrer.repository.ClassroomRepository;
import com.masferrer.repository.StudentRepository;
import com.masferrer.repository.StudentXClassroomRepository;
import com.masferrer.services.StudentService;

import jakarta.transaction.Transactional;

@Service
public class StudentServiceImpl implements StudentService{

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private StudentXClassroomRepository studentXClassroomRepository;

    @Autowired
    private ClassroomRepository classroomRepository;

    @Override
    public List<Student> findAll() {
        Sort sort = Sort.by(Sort.Direction.ASC, "name");
        return studentRepository.findAll(sort);
    }

    @Override
    public Page<Student> findAll(int pageNo, int pageSize) {
        Sort sort = Sort.by(Sort.Direction.ASC, "name");
        Pageable pageable = PageRequest.of(pageNo, pageSize,sort);
        return studentRepository.findAll(pageable);
    }

    @Override
    public List<Student> findStudentsByClassroom(FindClassroomDTO info, Grade grade, Shift shift){
        Classroom foundClassroom = classroomRepository.findByYearAndGradeAndShift(info.getYear(), grade, shift);

        if(foundClassroom == null) {
            return null;
        }

        return studentXClassroomRepository.findStudentsByClassroomId(foundClassroom.getId());
    }

    @Override
    public List<Student> findNewStudents() {
        return studentXClassroomRepository.findStudentsNotInAnyClassroom();
    }

    @Override
    public Student findById(UUID id) {
        return studentRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public Boolean save(SaveStudentDTO info) {
        Student studentFound = studentRepository.findByNieOrName(info.getNie(), info.getName());

        if(studentFound != null) {
            return false;
        }

        Student student = new Student(info.getNie(), info.getName());
        studentRepository.save(student);
        return true;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public Boolean update(UpdateStudentDTO info, UUID id) {
        Student studentToUpdate = studentRepository.findById(id).orElse(null);
        
        if(studentToUpdate == null) {
            return false;
        }

        List<Student> existingStudent = studentRepository.findAllByNieOrName(info.getNie(), info.getName());
        existingStudent.removeIf(student -> student.getId().equals(id));

        if (!existingStudent.isEmpty()) {
            // The name or nie is already used by another student
            return false;
        }

        studentToUpdate.setNie(info.getNie() != null && !(info.getNie().trim().isEmpty()) ? info.getNie() : studentToUpdate.getNie());
        studentToUpdate.setName(info.getName() != null && !(info.getName().trim().isEmpty()) ? info.getName() : studentToUpdate.getName());
        studentRepository.save(studentToUpdate);

        return true;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public Boolean toggleActiveStatus(UUID id) {
        Student student = studentRepository.findById(id).orElse(null);
        if (student == null) {
            return false;
        }
        student.setActive(!student.getActive());
        studentRepository.save(student);
        return true;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public Boolean delete(UUID id) {
        Student studentToDelete = studentRepository.findById(id).orElse(null);

        if(studentToDelete == null) {
            return false;
        }

        studentRepository.delete(studentToDelete);
        return true;
    }
}
