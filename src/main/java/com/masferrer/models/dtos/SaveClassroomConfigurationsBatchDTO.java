package com.masferrer.models.dtos;

import java.time.LocalTime;
import java.util.UUID;
import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveClassroomConfigurationsBatchDTO {

    @NotNull(message = "classConfigs is required")
    private List<SaveClassroomConfigurationDTO> classConfigurations;

    @NotNull(message = "classrooms is required")
    private List<UUID> classrooms;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SaveClassroomConfigurationDTO {
        @NotNull(message = "idClassPeriod is required")
        private UUID idClassPeriod;

        @NotNull(message = "hourStart is required")
        private LocalTime hourStart;

        @NotNull(message = "hourEnd is required")
        private LocalTime hourEnd;
    }
}
