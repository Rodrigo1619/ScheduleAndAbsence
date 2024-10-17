package com.masferrer.services;

import java.util.List;
import java.util.UUID;

import com.masferrer.models.dtos.ClassConfigurationDTO;
import com.masferrer.models.dtos.ClassroomConfigurationListDTO;
import com.masferrer.models.dtos.SaveClassroomConfigurationsBatchDTO;
import com.masferrer.models.dtos.UpdateClassroomConfigurationDTO;

public interface ClassroomConfigurationService {
    List<ClassroomConfigurationListDTO> findAll();
    ClassConfigurationDTO findById(UUID id);
    List<ClassroomConfigurationListDTO> findByClassroomId(UUID classroomId);
    List<ClassroomConfigurationListDTO> findByShiftAndYear(UUID shiftId, String year);
    List<ClassroomConfigurationListDTO> saveAll(SaveClassroomConfigurationsBatchDTO info) throws Exception;
    List<ClassroomConfigurationListDTO> updateAll(List<UpdateClassroomConfigurationDTO> classConfigs) throws Exception;
    void delete(UUID id) throws Exception;
    void deleteAll(List<UUID> ids) throws Exception;
}
