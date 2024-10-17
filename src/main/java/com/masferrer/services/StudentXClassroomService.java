package com.masferrer.services;

import java.util.UUID;
import java.util.List;

import com.masferrer.models.dtos.AddNewStudentsToClassroomDTO;
import com.masferrer.models.dtos.ClassroomEnrollmentsDTO;
import com.masferrer.models.dtos.ClassroomWithStudentsDTO;
import com.masferrer.models.dtos.EnrollStudentsToClassroomDTO;
import com.masferrer.models.dtos.StudentXClassroomDTO;

public interface StudentXClassroomService {
    List<StudentXClassroomDTO> findAll();
    List<StudentXClassroomDTO> addStudentsToClassroom(AddNewStudentsToClassroomDTO info) throws Exception;
    List<StudentXClassroomDTO> changeStudentsToOtherClassroom(EnrollStudentsToClassroomDTO info) throws Exception;
    List<StudentXClassroomDTO> enrollStudentsToClassroom(EnrollStudentsToClassroomDTO info) throws Exception;
    List<ClassroomEnrollmentsDTO> findEnrollmentsByClassroom(UUID gradeId, UUID shiftId, String year);
    ClassroomWithStudentsDTO findStudentsByUserNie(String nie, String year);
}
