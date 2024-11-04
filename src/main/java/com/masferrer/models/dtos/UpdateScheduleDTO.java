package com.masferrer.models.dtos;

import java.util.UUID;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateScheduleDTO {
        private UUID id_schedule;
        private UUID id_user;
        private UUID id_subject;
        private UUID id_classroomConfiguration;
        private UUID id_weekday;
}
