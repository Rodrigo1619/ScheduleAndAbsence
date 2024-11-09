package com.masferrer.services;

import java.util.List;
import java.util.UUID;

import com.masferrer.models.dtos.ClassConfigurationDTO;
import com.masferrer.models.dtos.CreateScheduleListDTO;
import com.masferrer.models.dtos.ScheduleDTO;
import com.masferrer.models.dtos.ScheduleListDTO;
import com.masferrer.models.dtos.UpdateScheduleDTO;
import com.masferrer.models.dtos.UpdateScheduleListDTO;
import com.masferrer.models.entities.Schedule;

public interface ScheduleService {
    List<ScheduleListDTO> createSchedule(CreateScheduleListDTO createScheduleListDTO) throws Exception;
    List<Schedule> updateSchedule(List<UpdateScheduleDTO> updateScheduleDTO) throws Exception;
    List<ScheduleListDTO> updateSchedule(UpdateScheduleListDTO schedules) throws Exception;
    void deleteSchedule(List<UUID> schedulesIds) throws Exception;
    List<ScheduleListDTO> getSchedulesByUserIdAndYear(UUID userId, int year);
    List<ScheduleListDTO> getSchedulesByUserIdAndShiftAndYear(UUID userId, UUID shiftId, String year);
    List<ScheduleListDTO> getSchedulesByUserTokenAndShiftAndYear(UUID shiftId, String year);
    List<ScheduleListDTO> getScheduleByClassroomId(UUID classroomId);
    List<ScheduleListDTO> findAll();
    ScheduleDTO<ClassConfigurationDTO> findScheduleByParameters(UUID classperiodId, UUID shiftId, UUID weekdayId, String year, UUID userId,UUID classroomId);
}
