package com.masferrer.services;

import java.util.List;
import java.util.UUID;

import com.masferrer.models.dtos.CustomClassroomDTO;
import com.masferrer.models.dtos.PageDTO;
import com.masferrer.models.dtos.SaveClassroomDTO;
import com.masferrer.models.dtos.UpdateClassroomDTO;
import com.masferrer.models.entities.Classroom;
import com.masferrer.models.entities.Student;

public interface ClassroomService {
    List<CustomClassroomDTO> findAll();
    PageDTO<CustomClassroomDTO> findAll(int page, int size);
    List<CustomClassroomDTO> findAllByShiftAndYear(UUID shiftId, String year);
    CustomClassroomDTO findById(UUID id);
    CustomClassroomDTO findByParameters(UUID idGrade, String year);
    List<Student> findStudentsByClassroom(UUID classroomId);
    CustomClassroomDTO save(SaveClassroomDTO info);
    CustomClassroomDTO update(UpdateClassroomDTO info, UUID classsroomId);
    void delete(UUID id);
    List<CustomClassroomDTO> findByUserAndYearAndShift(String year, UUID shiftId);
    List<CustomClassroomDTO> findByUserAndYear(String year);
    List<Classroom> getClassroomsByUser(UUID userId);
}
