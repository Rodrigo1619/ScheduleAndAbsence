package com.masferrer.services;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.masferrer.models.dtos.AbsenceRecordDTO;
import com.masferrer.models.dtos.AbsenceRecordWithStudentsAttendanceDTO;
import com.masferrer.models.dtos.AbsenceRecordWithStudentsDTO;
import com.masferrer.models.dtos.CreateAbsentRecordDTO;
import com.masferrer.models.dtos.EditAbsenceRecordDTO;
import com.masferrer.models.dtos.StudentAbsenceCountDTO;
import com.masferrer.models.dtos.TopStudentsByShiftDTO;
import com.masferrer.models.entities.AbsenceRecord;

public interface AbsenceRecordService {

    List<AbsenceRecordDTO> findAll();
    AbsenceRecord findById(UUID id);
    AbsenceRecordDTO createAbsenceRecord(CreateAbsentRecordDTO createAbsentRecordDTO) throws Exception;
    Boolean toggleTeacherValidation(UUID idAbsenceRecord) throws Exception;
    Boolean toggleCoordinationValidation(UUID idAbsenceRecord) throws Exception;
    AbsenceRecordWithStudentsDTO editAbsenceRecord(EditAbsenceRecordDTO info, UUID id) throws Exception;
    void deleteAbsenceStudents(List<UUID> absentStudentsids) throws Exception;
    long findByDate(LocalDate date);
    List<AbsenceRecordDTO> findByDateNoStudent(LocalDate date);
    List<AbsenceRecordDTO> findByDateAndShift(LocalDate date, UUID idShift);
    List<AbsenceRecordDTO> findByMonthAndYear(int month, int year);
    List<AbsenceRecordDTO> findByClassroom(UUID idClassroom);
    AbsenceRecordDTO findByDateAndClassroom(LocalDate date, UUID idClassrooms);
    AbsenceRecordWithStudentsAttendanceDTO findByDateAndClassroomWithStudents(LocalDate date, UUID idClassrooms);
    List<AbsenceRecordWithStudentsDTO> findByClassroomAndShift(UUID idClassroom, UUID shift);
    List<AbsenceRecord> findByClassroomAndShiftAndYear(UUID idClassroom, UUID idShift, int year);
    List<AbsenceRecordDTO> findByUserAndDate(UUID userId, LocalDate date);
    List<StudentAbsenceCountDTO> getTopAbsentStudentsByClassroom(UUID classroomId, String year);
    List<StudentAbsenceCountDTO> getAbsentStudentsCountByClassroom(UUID classroomId, String year);
    List<StudentAbsenceCountDTO> getTopAbsenceStudentsCountByTokenAndShift(UUID userId, UUID shift, String year);
    TopStudentsByShiftDTO getTopAbsentStudentsByMonth(LocalDate date);
    List<StudentAbsenceCountDTO> getAllAbsenceStudentByUserAndShift(UUID userId, UUID shift, String year);
    
}
