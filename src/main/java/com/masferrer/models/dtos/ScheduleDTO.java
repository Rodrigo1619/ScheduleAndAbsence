package com.masferrer.models.dtos;

import java.util.UUID;

import com.masferrer.models.entities.Weekday;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleDTO {

    private UUID id;
    private UserXSubjectDTO user_x_subject;
    private ShortClassroomConfigurationDTO classroomConfiguration;
    private Weekday weekday;
}
