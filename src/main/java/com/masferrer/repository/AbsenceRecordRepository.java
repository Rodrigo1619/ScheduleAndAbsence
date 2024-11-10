package com.masferrer.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.masferrer.models.entities.AbsenceRecord;
import com.masferrer.models.entities.Classroom;

public interface AbsenceRecordRepository extends JpaRepository<AbsenceRecord, UUID> {
    @Query("SELECT absrec FROM AbsenceRecord absrec WHERE absrec.date = :date AND absrec.classroom = :classroom")
    AbsenceRecord findByDateAndClassroom(@Param("date") LocalDate date, @Param("classroom") Classroom classroom);

    List<AbsenceRecord> findByDate(LocalDate date);

    @Query("SELECT COUNT(absrec) FROM AbsenceRecord absrec WHERE absrec.date = :date AND absrec.coordinationValidation = false")
    long countAbsenceRecordsWithoutCoordinationValidation(@Param("date") LocalDate date);
    
    List<AbsenceRecord> findByClassroom(Classroom classroom);
    
    @Query("SELECT absrec FROM AbsenceRecord absrec WHERE MONTH(absrec.date) = :month AND YEAR(absrec.date) = :year")
    List<AbsenceRecord> findByMonthAndYear(@Param("month") int month, @Param("year") int year);

    @Query("SELECT absrec FROM AbsenceRecord absrec WHERE absrec.classroom.id = :classroomId AND absrec.classroom.grade.shift.id = :shiftId")
    List<AbsenceRecord> findByClassroomAndShift(@Param("classroomId") UUID classroomId, @Param("shiftId") UUID shiftId);

    @Query("SELECT absrec FROM AbsenceRecord absrec WHERE absrec.classroom = :classroom AND absrec.classroom.grade.shift.id = :shiftId AND YEAR(absrec.date) = :year")
    List<AbsenceRecord> findByClassroomAndShiftAndYear(@Param("classroom") Classroom classroom, @Param("shiftId") UUID shiftId, @Param("year") int year);

    @Query("SELECT absrec FROM AbsenceRecord absrec WHERE absrec.date = :date AND absrec.classroom.grade.shift.id = :shiftId")
    List<AbsenceRecord> findByDateAndShift(@Param("date") LocalDate date, @Param("shiftId") UUID shiftId);

    @Query("SELECT absrec FROM AbsenceRecord absrec WHERE absrec.date = :date AND absrec.classroom.user.id = :userId")
    List<AbsenceRecord> findByUserAndDate(@Param("userId") UUID userId, @Param("date") LocalDate date);
}
