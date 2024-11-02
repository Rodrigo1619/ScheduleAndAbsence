package com.masferrer.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.masferrer.models.entities.AbsenceRecord;
import com.masferrer.models.entities.AbsentStudent;

public interface AbsentStudentRepository extends JpaRepository<AbsentStudent, UUID>{
    void deleteByAbsenceRecord(AbsenceRecord absenceRecord);

    @Query("SELECT s.student, " +
        "SUM(CASE WHEN s.code.description = 'Injustificada' THEN 1 ELSE 0 END) as unjustifiedAbsences, " +
        "SUM(CASE WHEN s.code.description != 'Injustificada' THEN 1 ELSE 0 END) as justifiedAbsences " +
        "FROM AbsentStudent s " +
        "WHERE s.absenceRecord.classroom.id = :classroomId " +
        "AND s.absenceRecord.classroom.year = :year " +
        "GROUP BY s.student " +
        "ORDER BY unjustifiedAbsences DESC")
    List<Object[]> findTopAbsentStudentsByClassroom(@Param("classroomId") UUID classroomId, @Param("year") String year, Pageable pageable);


    @Query("SELECT s.student, " +
        "SUM(CASE WHEN s.code.description = 'Injustificada' THEN 1 ELSE 0 END) as unjustifiedAbsences, " +
        "SUM(CASE WHEN s.code.description != 'Injustificada' THEN 1 ELSE 0 END) as justifiedAbsences " +
        "FROM AbsentStudent s " +
        "WHERE s.absenceRecord.classroom.id = :classroomId " +
        "AND s.absenceRecord.classroom.year = :year " +
        "GROUP BY s.student " +
        "ORDER BY unjustifiedAbsences DESC")
    List<Object[]> findAllAbsentStudentByClassroomWithAbsenceType(@Param("classroomId") UUID classroomId, @Param("year") String year);

    @Query("SELECT s.student, " +
        "SUM(CASE WHEN s.code.description = 'Injustificada' THEN 1 ELSE 0 END) as unjustifiedAbsences, " +
        "SUM(CASE WHEN s.code.description != 'Injustificada' THEN 1 ELSE 0 END) as justifiedAbsences " +
        "FROM AbsentStudent s " +
        "WHERE s.absenceRecord.classroom.id IN :classroomIds " +
        "AND s.absenceRecord.classroom.grade.shift.id = :shiftId " +
        "AND s.absenceRecord.classroom.year = :year " +
        "GROUP BY s.student " +
        "ORDER BY SUM(CASE WHEN s.code.description = 'Injustificada' THEN 1 ELSE 0 END) DESC")
    List<Object[]> findTopAbsentStudentsByClassroomsAndShiftAndYear(
        @Param("classroomIds") List<UUID> classroomIds,
        @Param("shiftId") UUID shiftId,
        @Param("year") String year,
        Pageable pageable);

    @Query("SELECT s.student, " +
        "SUM(CASE WHEN s.code.description = 'Injustificada' THEN 1 ELSE 0 END) as unjustifiedAbsences, " +
        "SUM(CASE WHEN s.code.description != 'Injustificada' THEN 1 ELSE 0 END) as justifiedAbsences " +
        "FROM AbsentStudent s " +
        "WHERE s.absenceRecord.classroom.id IN :classroomIds " +
        "AND s.absenceRecord.classroom.grade.shift.id = :shiftId " +
        "AND s.absenceRecord.classroom.year = :year " +
        "GROUP BY s.student " +
        "ORDER BY SUM(CASE WHEN s.code.description = 'Injustificada' THEN 1 ELSE 0 END) DESC")
    List<Object[]> findAllAbsentStudentsByClassroomsAndShiftAndYear(
        @Param("classroomIds") List<UUID> classroomIds,
        @Param("shiftId") UUID shiftId,
        @Param("year") String year);
}
