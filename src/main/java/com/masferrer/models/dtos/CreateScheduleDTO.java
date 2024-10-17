package com.masferrer.models.dtos;

import java.time.LocalTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CreateScheduleDTO {
    private LocalTime hourStart;
    private LocalTime hourEnd;
    private UUID id_user_x_subject;
    private UUID id_classroom;
    private UUID id_weekday;

}
