package com.masferrer.services;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;

import com.masferrer.models.dtos.FindClassroomDTO;
import com.masferrer.models.dtos.SaveClassroomDTO;
import com.masferrer.models.dtos.UpdateClassroomDTO;
import com.masferrer.models.entities.Classroom;
import com.masferrer.models.entities.Grade;
import com.masferrer.models.entities.Shift;
import com.masferrer.models.entities.Student;
import com.masferrer.models.entities.User;

public interface ClassroomService {
    List<Classroom> findAll();
    Page<Classroom> findAll(int page, int size);
    List<Classroom> findAllByShiftAndYear(UUID shiftId, String year);
    Classroom findById(UUID id);
    Classroom findByParameters(FindClassroomDTO parameters);
    Classroom findByParameters(String year, Grade grade, Shift shift);
    List<Student> findStudentsByClassroom(UUID classroomId);
    Boolean save(SaveClassroomDTO info, Grade grade, Shift shift, User teacher);
    Boolean update(UpdateClassroomDTO info,UUID id);
    Boolean delete(UUID id);
    List<Classroom> findByUserAndYearAndShift(UUID userId, String year, Shift shift);
    List<Classroom> findByUserAndYear(UUID userId, String year);
    List<Classroom> getClassroomsByUser(UUID userId);
}
