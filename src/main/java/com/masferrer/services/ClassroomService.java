package com.masferrer.services;

import java.util.List;
import java.util.UUID;

import com.masferrer.models.dtos.CustomClassroomDTO;
import com.masferrer.models.dtos.FindClassroomDTO;
import com.masferrer.models.dtos.PageDTO;
import com.masferrer.models.dtos.SaveClassroomDTO;
import com.masferrer.models.dtos.UpdateClassroomDTO;
import com.masferrer.models.entities.Classroom;
import com.masferrer.models.entities.Grade;
import com.masferrer.models.entities.Shift;
import com.masferrer.models.entities.Student;

public interface ClassroomService {
    List<CustomClassroomDTO> findAll();
    PageDTO<CustomClassroomDTO> findAll(int page, int size);
    List<CustomClassroomDTO> findAllByShiftAndYear(UUID shiftId, String year);
    CustomClassroomDTO findById(UUID id);
    Classroom findByParameters(FindClassroomDTO parameters);
    Classroom findByParameters(String year, Grade grade, Shift shift);
    List<Student> findStudentsByClassroom(UUID classroomId);
    CustomClassroomDTO save(SaveClassroomDTO info);
    CustomClassroomDTO update(UpdateClassroomDTO info,UUID id);
    void delete(UUID id);
    List<Classroom> findByUserAndYearAndShift(UUID userId, String year, Shift shift);
    List<Classroom> findByUserAndYear(UUID userId, String year);
    List<Classroom> getClassroomsByUser(UUID userId);
}
