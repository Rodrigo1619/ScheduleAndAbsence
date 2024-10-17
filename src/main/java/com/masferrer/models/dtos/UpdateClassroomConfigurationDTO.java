package com.masferrer.models.dtos;

import java.time.LocalTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateClassroomConfigurationDTO {

    private UUID idClassroomConfiguration;
    private UUID idClassPeriod;
    private LocalTime hourStart;
    private LocalTime hourEnd;
    private UUID idClassroom;
}
