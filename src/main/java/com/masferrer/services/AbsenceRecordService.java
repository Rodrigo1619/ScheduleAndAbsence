package com.masferrer.services;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.masferrer.models.dtos.AbsenceRecordDTO;
import com.masferrer.models.dtos.AbsenceRecordWithStudentsDTO;
import com.masferrer.models.dtos.CreateAbsentRecordDTO;
import com.masferrer.models.dtos.EditAbsenceRecordDTO;
import com.masferrer.models.dtos.StudentAbsenceCountDTO;
import com.masferrer.models.entities.AbsenceRecord;

public interface AbsenceRecordService {

    List<AbsenceRecordDTO> findAll();
    AbsenceRecord findById(UUID id);
    AbsenceRecordDTO createAbsenceRecord(CreateAbsentRecordDTO createAbsentRecordDTO) throws Exception;
    Boolean toggleTeacherValidation(UUID idAbsenceRecord) throws Exception;
    Boolean toggleCoordinationValidation(UUID idAbsenceRecord) throws Exception;
    AbsenceRecordDTO editAbsenceRecord(EditAbsenceRecordDTO info, UUID id) throws Exception;
    List<AbsenceRecordWithStudentsDTO> findByDate(LocalDate date);
    List<AbsenceRecordDTO> findByDateNoStudent(LocalDate date);
    List<AbsenceRecordDTO> findByDateAndShift(LocalDate date, UUID idShift);
    List<AbsenceRecordDTO> findByMonthAndYear(int month, int year);
    List<AbsenceRecordDTO> findByClassroom(UUID idClassroom);
    AbsenceRecordDTO findByDateAndClassroom(LocalDate date, UUID idClassrooms);
    List<AbsenceRecordWithStudentsDTO> findByClassroomAndShift(UUID idClassroom, UUID shift);
    List<AbsenceRecord> findByClassroomAndShiftAndYear(UUID idClassroom, UUID idShift, int year);
    List<AbsenceRecordDTO> findByUserAndDate(UUID userId, LocalDate date);
    List<StudentAbsenceCountDTO> getTopAbsentStudentsByClassroom(UUID classroomId, String year);
    List<StudentAbsenceCountDTO> getAllAbsentStudentByClassroom(UUID classroomId, String year);
    List<StudentAbsenceCountDTO> getTopAbsenceStudentByUserAndShift(UUID userId, UUID shift, String year);
    List<StudentAbsenceCountDTO> getAllAbsenceStudentByUserAndShift(UUID userId, UUID shift, String year);
    
}
