package com.masferrer.services;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.masferrer.models.dtos.AbsenceRecordWithStudentsDTO;
import com.masferrer.models.dtos.CreateAbsentRecordDTO;
import com.masferrer.models.dtos.EditAbsenceRecordDTO;
import com.masferrer.models.dtos.StudentAbsenceCountDTO;
import com.masferrer.models.entities.AbsenceRecord;

public interface AbsenceRecordService {

    List<AbsenceRecord> findAll();
    AbsenceRecord findById(UUID id);
    AbsenceRecord createAbsenceRecord(CreateAbsentRecordDTO createAbsentRecordDTO) throws Exception;
    Boolean toggleTeacherValidation(UUID idAbsenceRecord) throws Exception;
    Boolean toggleCoordinationValidation(UUID idAbsenceRecord) throws Exception;
    AbsenceRecord editAbsenceRecord(EditAbsenceRecordDTO info, UUID id) throws Exception;
    List<AbsenceRecordWithStudentsDTO> findByDate(LocalDate date);
    List<AbsenceRecord> findByDateNoStudent(LocalDate date);
    List<AbsenceRecord> findByDateAndShift(LocalDate date, UUID idShift);
    List<AbsenceRecord> findByMonthAndYear(int month, int year);
    List<AbsenceRecord> findByClassroom(UUID idClassroom);
    AbsenceRecord findByDateAndClassroom(LocalDate date, UUID idClassrooms);
    List<AbsenceRecordWithStudentsDTO> findByClassroomAndShift(UUID idClassroom, UUID shift);
    List<AbsenceRecord> findByClassroomAndShiftAndYear(UUID idClassroom, UUID idShift, int year);
    List<AbsenceRecord> findByUserAndDate(UUID userId, LocalDate date);
    List<StudentAbsenceCountDTO> getTopAbsentStudentsByClassroom(UUID classroomId, String year);
    List<StudentAbsenceCountDTO> getAllAbsentStudentByClassroom(UUID classroomId, String year);
    List<StudentAbsenceCountDTO> getTopAbsenceStudentByUserAndShift(UUID userId, UUID shift, String year);
    List<StudentAbsenceCountDTO> getAllAbsenceStudentByUserAndShift(UUID userId, UUID shift, String year);
    
}
