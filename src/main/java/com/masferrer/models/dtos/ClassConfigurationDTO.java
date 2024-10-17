package com.masferrer.models.dtos;

import java.util.UUID;

import com.masferrer.models.entities.ClassPeriod;

import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClassConfigurationDTO {
    private UUID id;
    private LocalTime hourStart;
    private LocalTime hourEnd;
    private ClassPeriod classPeriod;
    private CustomClassroomDTO classroom;

}
