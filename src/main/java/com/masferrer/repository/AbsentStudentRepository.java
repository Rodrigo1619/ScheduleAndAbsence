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
        "SUM(CASE WHEN s.code.description = 'Otro, No justificado' THEN 1 ELSE 0 END) as unjustifiedAbsences, " +
        "SUM(CASE WHEN s.code.description != 'Otro, No justificado' THEN 1 ELSE 0 END) as justifiedAbsences " +
        "FROM AbsentStudent s " +
        "WHERE s.absenceRecord.classroom.id = :classroomId " +
        "AND s.absenceRecord.classroom.year = :year " +
        "GROUP BY s.student " +
        "ORDER BY unjustifiedAbsences DESC")
    List<Object[]> findTopAbsentStudentsByClassroom(@Param("classroomId") UUID classroomId, @Param("year") String year, Pageable pageable);

    @Query("SELECT st, " +
        "COALESCE((SELECT COUNT(s1) " +
        " FROM AbsentStudent s1 " +
        " WHERE s1.student.id = st.id " +
        "   AND s1.code.description = 'Otro, No justificado' " +
        "   AND s1.absenceRecord.classroom.id = :classroomId), 0) AS unjustifiedAbsences, " +
        "COALESCE((SELECT COUNT(s2) " +
        " FROM AbsentStudent s2 " +
        " WHERE s2.student.id = st.id " +
        "   AND s2.code.description != 'Otro, No justificado' " +
        "   AND s2.absenceRecord.classroom.id = :classroomId), 0) AS justifiedAbsences " +
        "FROM Student st " +
        "JOIN Student_X_Classroom sc ON st.id = sc.student.id " +
        "WHERE sc.classroom.id = :classroomId " +
        "GROUP BY st.id, st.name " +
        "ORDER BY unjustifiedAbsences DESC, st.name ASC")
    List<Object[]> findAllAbsentStudentByClassroomWithAbsenceType(@Param("classroomId") UUID classroomId);

    @Query("SELECT s.student, s.absenceRecord.classroom, " +
        "SUM(CASE WHEN s.code.description = 'Otro, No justificado' THEN 1 ELSE 0 END) as unjustifiedAbsences, " +
        "SUM(CASE WHEN s.code.description != 'Otro, No justificado' THEN 1 ELSE 0 END) as justifiedAbsences, " +
        "COUNT(s) AS totalAbsences " +
        "FROM AbsentStudent s " +
        "WHERE s.absenceRecord.classroom.id IN :classroomIds " +
        "AND s.absenceRecord.classroom.grade.shift.id = :shiftId " +
        "AND s.absenceRecord.classroom.year = :year " +
        "GROUP BY s.student, s.absenceRecord.classroom " +
        "ORDER BY totalAbsences DESC")
    List<Object[]> findTopAbsentStudentsByClassroomsAndShiftAndYear(
        @Param("classroomIds") List<UUID> classroomIds,
        @Param("shiftId") UUID shiftId,
        @Param("year") String year,
        Pageable pageable);

    @Query("SELECT s.student, " +
        "SUM(CASE WHEN s.code.description = 'Otro, No justificado' THEN 1 ELSE 0 END) as unjustifiedAbsences, " +
        "SUM(CASE WHEN s.code.description != 'Otro, No justificado' THEN 1 ELSE 0 END) as justifiedAbsences " +
        "FROM AbsentStudent s " +
        "WHERE s.absenceRecord.classroom.id IN :classroomIds " +
        "AND s.absenceRecord.classroom.grade.shift.id = :shiftId " +
        "AND s.absenceRecord.classroom.year = :year " +
        "GROUP BY s.student " +
        "ORDER BY SUM(CASE WHEN s.code.description = 'Otro, No justificado' THEN 1 ELSE 0 END) DESC")
    List<Object[]> findAllAbsentStudentsByClassroomsAndShiftAndYear(
        @Param("classroomIds") List<UUID> classroomIds,
        @Param("shiftId") UUID shiftId,
        @Param("year") String year);

    @Query("SELECT s.student, s.absenceRecord.classroom, COUNT(s) as totalAbsences " +
           "FROM AbsentStudent s " +
           "WHERE MONTH(s.date) = :month AND YEAR(s.date) = :year AND s.absenceRecord.classroom.grade.shift.id = :shiftId " +
           "AND s.student.active = true " +
           "GROUP BY s.student, s.absenceRecord.classroom " +
           "ORDER BY totalAbsences DESC")
    List<Object[]> findTopStudentAbsencesByMonthAndShift(@Param("month") int month, @Param("year") int year, @Param("shiftId") UUID shiftId);
}
