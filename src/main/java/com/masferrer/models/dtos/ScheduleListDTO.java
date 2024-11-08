package com.masferrer.models.dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleListDTO {

    private CustomClassroomDTO classroom;
    private List<ScheduleDTO<ShortClassroomConfigurationDTO>> schedules;
}
