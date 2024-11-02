package com.masferrer.repository;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.masferrer.models.entities.ClassroomConfiguration;

public interface ClassroomConfigurationRepository extends JpaRepository<ClassroomConfiguration, UUID> {
    List<ClassroomConfiguration> findByClassroomId(UUID classroomId, Sort sort);   

    @Query("SELECT cc FROM ClassroomConfiguration cc WHERE cc.classroom.grade.shift.id = :shiftId AND cc.classroom.year = :year")
    List<ClassroomConfiguration> findByShiftAndYear(@Param("shiftId") UUID shiftId, @Param("year") String year, Sort sort);
    
    //Validaciones
    ClassroomConfiguration findByClassPeriodIdAndHourStartAndHourEndAndClassroomId(UUID classPeriodId, LocalTime hourStart, LocalTime hourEnd, UUID classroomId);

    ClassroomConfiguration findByClassroomIdAndHourStartOrClassroomIdAndHourEnd(UUID classroomId, LocalTime hourStart,UUID classroomId2, LocalTime hourEnd);

    @Query("SELECT CASE WHEN COUNT(cc) > 0 THEN true ELSE false END FROM ClassroomConfiguration cc WHERE cc.classroom.id = :classroomId AND (cc.hourStart < :hourEnd AND cc.hourEnd > :hourStart)")
    Boolean OverlapingInDatabaseOnSave(UUID classroomId, LocalTime hourStart, LocalTime hourEnd);

    @Query("SELECT CASE WHEN COUNT(cc) > 0 THEN true ELSE false END FROM ClassroomConfiguration cc WHERE cc.classroom.id = :classroomId AND (cc.hourStart < :hourEnd AND cc.hourEnd > :hourStart) AND cc.id <> :id")
    Boolean OverlapingInDatabaseOnUpdate(@Param("classroomId") UUID classroomId, @Param("hourStart") LocalTime hourStart, @Param("hourEnd") LocalTime hourEnd, @Param("id") UUID currentid);

}
