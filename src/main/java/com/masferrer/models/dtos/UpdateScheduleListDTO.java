package com.masferrer.models.dtos;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateScheduleListDTO {

    private List<UUID> deleteList;
    private List<ScheduleDTO> newSchedules;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScheduleDTO {
        private UUID id_user;
        private UUID id_subject;
        private UUID id_classroomConfiguration;
        private UUID id_weekday;
    }
}
