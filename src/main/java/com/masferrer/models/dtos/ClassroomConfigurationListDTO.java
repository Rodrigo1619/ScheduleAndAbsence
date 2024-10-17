package com.masferrer.models.dtos;

import java.util.List;


import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClassroomConfigurationListDTO {

    private CustomClassroomDTO classroom;
    private List<ShortClassroomConfigurationDTO> classroomConfigurations;
}
