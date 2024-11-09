package com.masferrer.repository;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.masferrer.models.entities.Schedule;

public interface ScheduleRepository extends JpaRepository<Schedule, UUID> {

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END " +
    "FROM Schedule s " +
    "WHERE s.user_x_subject.user.id = :userId " +
    "AND s.weekday.id = :weekdayId " +
    "AND s.classroomConfiguration.classroom.year = :year " +
    "AND (s.classroomConfiguration.hourStart < :hourEnd AND s.classroomConfiguration.hourEnd > :hourStart)")
    boolean existsOverlappingSchedule(@Param("userId") UUID userId,
                                    @Param("weekdayId") UUID weekdayId,
                                    @Param("year") String year,
                                    @Param("hourStart") LocalTime hourStart,
                                    @Param("hourEnd") LocalTime hourEnd);

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END " +
           "FROM Schedule s " +
           "WHERE s.classroomConfiguration.classroom.id = :classroomId " +
           "AND s.weekday.id = :weekdayId " +
           "AND s.user_x_subject.user.id <> :userId " +
           "AND (s.classroomConfiguration.hourStart < :hourEnd AND s.classroomConfiguration.hourEnd > :hourStart)")
    boolean existsConflictingScheduleForClassroom(@Param("classroomId") UUID classroomId, 
                                                  @Param("weekdayId") UUID weekdayId, 
                                                  @Param("userId") UUID userId, 
                                                  @Param("hourStart") LocalTime hourStart, 
                                                  @Param("hourEnd") LocalTime hourEnd);

    // esta consulta es para poder relacionar al usuario con el horario
    @Query("SELECT s FROM Schedule s WHERE s.user_x_subject.user.id = :userId AND s.classroomConfiguration.classroom.year = :year")
    List<Schedule> findSchedulesByUserIdAndYear(@Param("userId") UUID userId, @Param("year") int year);

    // consulta para poder encontrar el horario segun usuario, el turno y el año
    @Query("SELECT s FROM Schedule s WHERE s.user_x_subject.user.id = :userId AND s.classroomConfiguration.classroom.year = :year AND s.classroomConfiguration.classroom.grade.shift.id = :shiftId")
    List<Schedule> findSchedulesByUserAndShiftAndYear(@Param("userId") UUID userId, @Param("shiftId") UUID shiftId, @Param("year") String year);

    // consulta para poder encontrar el horario segun id de classroom
    @Query("SELECT s FROM Schedule s WHERE s.classroomConfiguration.classroom.id = :classroomId")
    List<Schedule> findSchedulesByClassroomId(@Param("classroomId") UUID classroomId);

    // Consulta para encontrar el horario utilizando el parametro opcional de usuario
    @Query("SELECT s FROM Schedule s WHERE " +
           "s.classroomConfiguration.classPeriod.id = :classPeriodId AND " +
           "s.classroomConfiguration.classroom.grade.shift.id = :shiftId AND " +
           "s.classroomConfiguration.classroom.year = :year AND " +
           "s.weekday.id = :weekdayId AND " +
           "s.user_x_subject.user.id = :userId")
    List<Schedule> findByClassPeriodShiftWeekdayYearUserId(@Param("classPeriodId") UUID classPeriodId, @Param("shiftId") UUID shiftId, 
        @Param("weekdayId") UUID weekdayId, @Param("year") String year, @Param("userId") UUID userId);

    // Consulta para encontrar el horario utilizando el parametro opcional de classroom
    @Query("SELECT s FROM Schedule s WHERE " +
        "s.classroomConfiguration.classPeriod.id = :classPeriodId AND " +
        "s.classroomConfiguration.classroom.grade.shift.id = :shiftId AND " +
        "s.classroomConfiguration.classroom.year = :year AND " +
        "s.weekday.id = :weekdayId AND " +
        "s.classroomConfiguration.classroom.id = :classroomId")
    List<Schedule> findByClassPeriodShiftWeekdayYearClassroomId(@Param("classPeriodId") UUID classPeriodId, @Param("shiftId") UUID shiftId,
        @Param("weekdayId") UUID weekdayId, @Param("year") String year, @Param("classroomId") UUID classroomId);

}
