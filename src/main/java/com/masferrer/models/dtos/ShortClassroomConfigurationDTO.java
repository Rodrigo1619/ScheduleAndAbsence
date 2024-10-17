package com.masferrer.models.dtos;

import java.time.LocalTime;
import java.util.UUID;

import com.masferrer.models.entities.ClassPeriod;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShortClassroomConfigurationDTO {

    private UUID id;
    private LocalTime hourStart;
    private LocalTime hourEnd;
    private ClassPeriod classPeriod;
}
