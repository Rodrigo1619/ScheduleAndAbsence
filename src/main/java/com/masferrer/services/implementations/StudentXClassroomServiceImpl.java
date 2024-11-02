package com.masferrer.services.implementations;

import java.util.List;
import java.util.UUID;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.masferrer.models.dtos.AddNewStudentsToClassroomDTO;
import com.masferrer.models.dtos.ClassroomEnrollmentsDTO;
import com.masferrer.models.dtos.ClassroomWithStudentsDTO;
import com.masferrer.models.dtos.CustomClassroomDTO;
import com.masferrer.models.dtos.EnrollStudentsToClassroomDTO;
import com.masferrer.models.dtos.StudentXClassroomDTO;
import com.masferrer.models.entities.Classroom;
import com.masferrer.models.entities.Student;
import com.masferrer.models.entities.Student_X_Classroom;
import com.masferrer.repository.ClassroomRepository;
import com.masferrer.repository.StudentRepository;
import com.masferrer.repository.StudentXClassroomRepository;
import com.masferrer.services.StudentXClassroomService;
import com.masferrer.utils.BadRequestException;
import com.masferrer.utils.EntityMapper;
import com.masferrer.utils.NotFoundException;

import jakarta.transaction.Transactional;

@Service
public class StudentXClassroomServiceImpl implements StudentXClassroomService{

    @Autowired
    private ClassroomRepository classroomRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private StudentXClassroomRepository studentXClassroomRepository;

    @Autowired
    private EntityMapper entityMapper;

    @Override
    public List<StudentXClassroomDTO> findAll() {
        List<Student_X_Classroom> enrollments = studentXClassroomRepository.findAll(Sort.by(Sort.Order.asc("student.name")));
        List<StudentXClassroomDTO> response = entityMapper.mapStudentXClassroomList(enrollments);
        
        return response;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public List<StudentXClassroomDTO> addStudentsToClassroom(AddNewStudentsToClassroomDTO info) throws Exception {
        Classroom foundClassroom = classroomRepository.findById(info.getIdClassroom())
            .orElseThrow(() -> new NotFoundException("Classroom not found"));

        List<Student_X_Classroom> validatedStudents = new ArrayList<Student_X_Classroom>();

        for(UUID studentId : info.getIdStudents()) {
            Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Student not found"));

            //Check if the student is already assigned to another classroom for the same year
            Student_X_Classroom assignment = studentXClassroomRepository.findByStudentAndYear(student.getId(), foundClassroom.getYear());
            if (assignment != null) {
                throw new BadRequestException("Student already assigned to a classroom in the same year");
            }

            Student_X_Classroom assignedStudent = new Student_X_Classroom(student, foundClassroom);
            assignedStudent.setAssigned(false);
            validatedStudents.add(assignedStudent);
        }

        validatedStudents = studentXClassroomRepository.saveAll(validatedStudents);
        List<StudentXClassroomDTO> response = entityMapper.mapStudentXClassroomList(validatedStudents);

        return response;        
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public List<StudentXClassroomDTO> changeStudentsToOtherClassroom(EnrollStudentsToClassroomDTO info) throws Exception {
        Classroom foundClassroom = classroomRepository.findById(info.getIdClassroom())
            .orElseThrow(() -> new NotFoundException("Classroom not found"));

        List<Student_X_Classroom> validatedStudents = new ArrayList<Student_X_Classroom>();

        for(UUID enrollmentId : info.getIdEnrollments()) {
            Student_X_Classroom currentEnrollment = studentXClassroomRepository.findById(enrollmentId)
                .orElseThrow(() -> new NotFoundException("Student enrollment not found"));

            currentEnrollment.setClassroom(foundClassroom);
            validatedStudents.add(currentEnrollment);
        }

        validatedStudents = studentXClassroomRepository.saveAll(validatedStudents);
        List<StudentXClassroomDTO> response = entityMapper.mapStudentXClassroomList(validatedStudents);

        return response;        
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public List<StudentXClassroomDTO> enrollStudentsToClassroom(EnrollStudentsToClassroomDTO info) throws Exception {
        Classroom foundClassroom = classroomRepository.findById(info.getIdClassroom())
            .orElseThrow(() -> new NotFoundException("Classroom not found"));

        List<Student_X_Classroom> validatedEnrollments = new ArrayList<Student_X_Classroom>();

        for(UUID enrollmentId : info.getIdEnrollments()) {
            Student_X_Classroom currentEnrollment = studentXClassroomRepository.findById(enrollmentId)
                .orElseThrow(() -> new NotFoundException("Student enrollment not found"));

            if(currentEnrollment.getClassroom().getYear().equals(foundClassroom.getYear())) {
                throw new BadRequestException("Student already enrolled to a classroom for the current year");
            }

            //Check if the student is already assigned to another classroom for the next year
            Student_X_Classroom nextYearEnrollment = studentXClassroomRepository.findByStudentAndYear(currentEnrollment.getStudent().getId(), foundClassroom.getYear());

            //If the student is not already assigned to a classroom for the next year, create a new enrollment
            if(nextYearEnrollment == null) {
                nextYearEnrollment = new Student_X_Classroom(currentEnrollment.getStudent(), foundClassroom);
                nextYearEnrollment.setAssigned(false);
                validatedEnrollments.add(nextYearEnrollment);
                currentEnrollment.setAssigned(true);
            }

            //If the student is already assigned to a classroom for the next year, update the enrollment to the new classroom
            nextYearEnrollment.setClassroom(foundClassroom);
            validatedEnrollments.add(nextYearEnrollment);
            currentEnrollment.setAssigned(true);
        }

        validatedEnrollments = studentXClassroomRepository.saveAll(validatedEnrollments);
        List<StudentXClassroomDTO> response = entityMapper.mapStudentXClassroomList(validatedEnrollments);

        return response;        
    }

    @Override
    public ClassroomWithStudentsDTO findClassmatesByStudentNie(String nie, String year) {      
        Student foundStudent = studentRepository.findByNie(nie);
        if(foundStudent == null) {
            throw new NotFoundException("Student not found");
        }  
        
        Classroom foundClassroom = studentXClassroomRepository.findByNieAndYear(nie,year);
        if(foundClassroom == null) {
            throw new NotFoundException("Student not enrolled in a classroom for this year");
        }

        List<Student> students = studentXClassroomRepository.findStudentsByClassroomId(foundClassroom.getId());
        if(students == null || students.isEmpty()) {
            throw new NotFoundException("No students found in the classroom");
        }

        ClassroomWithStudentsDTO response = new ClassroomWithStudentsDTO(
            entityMapper.map(foundClassroom),
            students
        );

        return response;
    }

    @Override
    public List<ClassroomEnrollmentsDTO> findEnrollmentsByClassroom(UUID classroomId) {        
        Classroom foundClassroom = classroomRepository.findById(classroomId)
            .orElseThrow(() -> new NotFoundException("Classroom not found"));

        List<Student_X_Classroom> enrollments = studentXClassroomRepository.findEnrollmentsByClassroomId(foundClassroom.getId());
        List<ClassroomEnrollmentsDTO> response = new ArrayList<>();

        for (Student_X_Classroom enrollment : enrollments) {
            Student_X_Classroom nextYearEnrollment = studentXClassroomRepository.findByStudentAndYear(enrollment.getStudent().getId(), String.valueOf(Integer.parseInt(foundClassroom.getYear()) + 1));
            CustomClassroomDTO nextYearClassroomDTO = nextYearEnrollment != null ? entityMapper.map(nextYearEnrollment.getClassroom()) : null;

            ClassroomEnrollmentsDTO dto = new ClassroomEnrollmentsDTO(
                enrollment.getId(),
                enrollment.getStudent(),
                entityMapper.map(enrollment.getClassroom()),
                enrollment.getAssigned(),
                nextYearClassroomDTO
            );

            response.add(dto);
        }
        return response;
    }

}
