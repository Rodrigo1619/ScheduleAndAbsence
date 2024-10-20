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
    AbsenceRecord findByDateAndClassroom(LocalDate date, Classroom classroom);
    List<AbsenceRecord> findByDate(LocalDate date);
    
    List<AbsenceRecord> findByClassroom(Classroom classroom);
    @Query("SELECT absrec FROM AbsenceRecord absrec WHERE MONTH(absrec.date) = :month AND YEAR(absrec.date) = :year")
    List<AbsenceRecord> findByMonthAndYear(@Param("month") int month, @Param("year") int year);

    @Query("SELECT absrec FROM AbsenceRecord absrec WHERE absrec.classroom.id = :classroomId AND absrec.classroom.shift.id = :shiftId")
    List<AbsenceRecord> findByClassroomAndShift(@Param("classroomId") UUID classroomId, @Param("shiftId") UUID shiftId);

    @Query("SELECT absrec FROM AbsenceRecord absrec WHERE absrec.classroom.id = :classroomId AND absrec.classroom.shift.id = :shiftId AND YEAR(absrec.date) = :year")
    List<AbsenceRecord> findByClassroomAndShiftAndYear(@Param("classroomId") Classroom classroomId, @Param("shiftId") UUID shiftId, @Param("year") int year);

    @Query("SELECT absrec FROM AbsenceRecord absrec WHERE absrec.date = :date AND absrec.classroom.shift.id = :shiftId")
    List<AbsenceRecord> findByDateAndShift(@Param("date") LocalDate date, @Param("shiftId") UUID shiftId);

    @Query("SELECT absrec FROM AbsenceRecord absrec WHERE absrec.date = :date AND absrec.classroom.user.id = :userId")
    List<AbsenceRecord> findByUserAndDate(@Param("userId") UUID userId, @Param("date") LocalDate date);
}
