package com.masferrer.services.implementations;

import java.util.List;
import java.util.UUID;
import java.util.ArrayList;
import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.masferrer.models.dtos.ClassConfigurationDTO;
import com.masferrer.models.dtos.ClassroomConfigurationListDTO;
import com.masferrer.models.dtos.SaveClassroomConfigurationsBatchDTO;
import com.masferrer.models.dtos.SaveClassroomConfigurationsBatchDTO.SaveClassroomConfigurationDTO;
import com.masferrer.models.dtos.UpdateClassroomConfigurationDTO;
import com.masferrer.models.entities.ClassPeriod;
import com.masferrer.models.entities.Classroom;
import com.masferrer.models.entities.ClassroomConfiguration;
import com.masferrer.repository.ClassPeriodRepository;
import com.masferrer.repository.ClassroomConfigurationRepository;
import com.masferrer.repository.ClassroomRepository;
import com.masferrer.services.ClassroomConfigurationService;
import com.masferrer.utils.BadRequestException;
import com.masferrer.utils.EntityMapper;
import com.masferrer.utils.NotFoundException;

import jakarta.transaction.Transactional;

@Service
public class ClassroomConfigurationServiceImpl implements ClassroomConfigurationService {
    
    @Autowired
    private ClassroomConfigurationRepository classroomConfigurationRepository;

    @Autowired
    private ClassPeriodRepository classPeriodRepository;

    @Autowired
    private ClassroomRepository classroomRepository;

    @Autowired
    private EntityMapper entityMapper;

    @Override
    public List<ClassroomConfigurationListDTO> findAll() {
        List<ClassroomConfiguration> configsFound = classroomConfigurationRepository.findAll(Sort.by(Sort.Direction.ASC,"classroom.year", "classroom.grade.name", "classPeriod.name"));
        
        List<ClassroomConfigurationListDTO> response = entityMapper.mapToClassroomConfigurationsListDTO(configsFound);

        return response;
    }

    @Override
    public ClassConfigurationDTO findById(UUID id) {
        ClassroomConfiguration found  = classroomConfigurationRepository.findById(id).orElseThrow(() -> new NotFoundException("Classroom configuration not found"));
        
        ClassConfigurationDTO response = entityMapper.map(found);

        return response;
    }

    @Override
    public List<ClassroomConfigurationListDTO> findByClassroomId(UUID classroomId) {
        List<ClassroomConfiguration> configsFound = classroomConfigurationRepository.findByClassroomId(classroomId, Sort.by(Sort.Direction.ASC, "classroom.grade.name", "classPeriod.name"));

        List<ClassroomConfigurationListDTO> response = entityMapper.mapToClassroomConfigurationsListDTO(configsFound);

        return response;
    }

    @Override
    public List<ClassroomConfigurationListDTO> findByShiftAndYear( UUID shiftId, String year) {
        if(year == null || year.isEmpty()){
            throw new BadRequestException("year is required");
        }
        List<ClassroomConfiguration> configsFound = classroomConfigurationRepository.findByShiftAndYear(shiftId, year, Sort.by(Sort.Direction.ASC, "classroom.grade.name", "classPeriod.name"));

        if(configsFound == null || configsFound.isEmpty()) {
            throw new NotFoundException("Classroom configuration not found");
        }

        List<ClassroomConfigurationListDTO> response = entityMapper.mapToClassroomConfigurationsListDTO(configsFound);

        return response;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public List<ClassroomConfigurationListDTO> saveAll(SaveClassroomConfigurationsBatchDTO dto) throws Exception {
        List<ClassroomConfiguration> validatedConfigs = new ArrayList<>();

        for (SaveClassroomConfigurationDTO config : dto.getClassConfigurations()) {
            ClassPeriod classPeriod = classPeriodRepository.findById(config.getIdClassPeriod())
                .orElseThrow(() -> new NotFoundException("Class period not found"));

            for (UUID classroomId : dto.getClassrooms()) {
                Classroom classroom = classroomRepository.findById(classroomId)
                    .orElseThrow(() -> new NotFoundException("Classroom not found"));

                ClassroomConfiguration newClassConfiguration = new ClassroomConfiguration(
                    config.getHourStart(),
                    config.getHourEnd(),
                    classPeriod,
                    classroom
                );

                saveClassroomConfigurationValidations(newClassConfiguration, dto.getClassConfigurations());
                validatedConfigs.add(newClassConfiguration);
            }
        }

        validatedConfigs = classroomConfigurationRepository.saveAll(validatedConfigs);

        List<ClassroomConfigurationListDTO> response = entityMapper.mapToClassroomConfigurationsListDTO(validatedConfigs);
        return response;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public List<ClassroomConfigurationListDTO> updateAll(List<UpdateClassroomConfigurationDTO> classConfigs) throws Exception {
        List<ClassroomConfiguration> processedConfigs = new ArrayList<>();

        for (UpdateClassroomConfigurationDTO info : classConfigs) {
            ClassroomConfiguration classConfigToUpdate;

            // Check if the configuration exists
            if (info.getIdClassroomConfiguration() != null) {
                classConfigToUpdate = classroomConfigurationRepository.findById(info.getIdClassroomConfiguration())
                        .orElse(new ClassroomConfiguration());
            } else {
                // Check if a configuration with the same details already exists
                classConfigToUpdate = classroomConfigurationRepository.findByClassPeriodIdAndHourStartAndHourEndAndClassroomId(
                    info.getIdClassPeriod(), info.getHourStart(), info.getHourEnd(), info.getIdClassroom());

                    if (classConfigToUpdate == null) {
                        classConfigToUpdate = new ClassroomConfiguration();
                    }
            }

            // Update or set classroom
            if (info.getIdClassroom() != null) {
                Classroom classroom = classroomRepository.findById(info.getIdClassroom())
                        .orElseThrow(() -> new NotFoundException("Classroom not found"));
                classConfigToUpdate.setClassroom(classroom);
            }

            // Update or set class period
            if (info.getIdClassPeriod() != null) {
                ClassPeriod classPeriod = classPeriodRepository.findById(info.getIdClassPeriod())
                        .orElseThrow(() -> new NotFoundException("Class period not found"));
                classConfigToUpdate.setClassPeriod(classPeriod);
            }

            // Update or set hour start
            if (info.getHourStart() != null) {
                classConfigToUpdate.setHourStart(info.getHourStart());
            }

            // Update or set hour end
            if (info.getHourEnd() != null) {
                classConfigToUpdate.setHourEnd(info.getHourEnd());
            }

            // Validations within the provided list
            for (UpdateClassroomConfigurationDTO otherConfig : classConfigs) {
            // Skip the current classConfigToUpdate itself
            if (classConfigToUpdate.getClassPeriod() != null && classConfigToUpdate.getClassPeriod().getId().equals(otherConfig.getIdClassPeriod()) &&
                classConfigToUpdate.getHourStart() != null && classConfigToUpdate.getHourStart().equals(otherConfig.getHourStart()) &&
                classConfigToUpdate.getHourEnd() != null && classConfigToUpdate.getHourEnd().equals(otherConfig.getHourEnd())) {
                continue;
            }

            // Check if another configuration with the same class period exists in the provided list
            if (classConfigToUpdate.getClassPeriod() != null && classConfigToUpdate.getClassPeriod().getId().equals(otherConfig.getIdClassPeriod()) &&
                !classConfigToUpdate.getClassPeriod().getName().equals("RECREO") &&
                classConfigToUpdate.getId() != null && !classConfigToUpdate.getId().equals(otherConfig.getIdClassroomConfiguration())) {
                throw new BadRequestException("A classroom configuration already exists with the same class period in the provided list");
            }

            // Check if overlapping hours exist with another configuration in the provided list
            if (classConfigToUpdate.getHourStart() != null && classConfigToUpdate.getHourEnd() != null &&
                otherConfig.getHourStart() != null && otherConfig.getHourEnd() != null &&
                classConfigToUpdate.getId() != null && !classConfigToUpdate.getId().equals(otherConfig.getIdClassroomConfiguration()) &&
                hoursOverlapping(classConfigToUpdate.getHourStart(), classConfigToUpdate.getHourEnd(), otherConfig.getHourStart(), otherConfig.getHourEnd())) {
                throw new BadRequestException("Classroom configuration overlap with another configuration within the provided list " +
                    classConfigToUpdate.getHourStart() + " - " + classConfigToUpdate.getHourEnd() + " with " + otherConfig.getHourStart() + " - " + otherConfig.getHourEnd());
                }
            }
            processedConfigs.add(classConfigToUpdate);
        }

        processedConfigs = classroomConfigurationRepository.saveAll(processedConfigs);
        List<ClassroomConfigurationListDTO> response = entityMapper.mapToClassroomConfigurationsListDTO(processedConfigs);

        return response;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void delete(UUID id) throws Exception {
        ClassroomConfiguration classroomConfiguration = classroomConfigurationRepository.findById(id).orElseThrow(() -> new NotFoundException("Classroom configuration not found"));
        classroomConfigurationRepository.delete(classroomConfiguration);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void deleteAll(List<UUID> classConfigsIds) throws Exception {
        List<ClassroomConfiguration> configs = classroomConfigurationRepository.findAllById(classConfigsIds);
        if (configs.size() != configs.size()) {
            throw new BadRequestException("Invalid schedule Id");
        }

        if(configs.size() == 0) {
            throw new BadRequestException("Id not provided");
        }
        classroomConfigurationRepository.deleteAll(configs);
    }

    private Boolean hoursOverlapping(LocalTime start1, LocalTime end1, LocalTime start2, LocalTime end2) {
        return (start1.isBefore(end2) && start2.isBefore(end1));
    }

    private void saveClassroomConfigurationValidations(ClassroomConfiguration newClassConfig, List<SaveClassroomConfigurationDTO> classConfigs) {
        sameClassPeriodValidation(newClassConfig);
        sameStartOrEndHourValidation(newClassConfig);
        checkHourOverlapInDatabaseOnSave(newClassConfig);

        for (SaveClassroomConfigurationDTO otherConfig : classConfigs) {
            // Skip the current newClassConfig itself
            if (newClassConfig.getClassPeriod().getId().equals(otherConfig.getIdClassPeriod()) &&
                newClassConfig.getHourStart().equals(otherConfig.getHourStart()) &&
                newClassConfig.getHourEnd().equals(otherConfig.getHourEnd())) {
                    continue;
            }
            // Check if exist other configuration with the same class period in the provided list
            if(newClassConfig.getClassPeriod().getId().equals(otherConfig.getIdClassPeriod()) && 
                !newClassConfig.getClassPeriod().getName().equals("RECREO")) {
                   throw new BadRequestException("A classroom configuration already exists with the same class period in the provided list");
            }

            // Check if exist overlapping hours with another configuration in the provided list
            if (hoursOverlapping(newClassConfig.getHourStart(), newClassConfig.getHourEnd(), otherConfig.getHourStart(), otherConfig.getHourEnd())) {
                    throw new BadRequestException("The hours of the classroom configuration overlap with another configuration in the provided list "
                    + newClassConfig.getHourStart() + " - " + newClassConfig.getHourEnd() + " with " + otherConfig.getHourStart() + " - " + otherConfig.getHourEnd());
            }
        }
    }

    private void checkHourOverlapInDatabaseOnSave(ClassroomConfiguration classConfig){
        Boolean conflict = classroomConfigurationRepository.OverlapingInDatabaseOnSave(
        // ClassroomConfiguration conflict = classroomConfigurationRepository.hourOverlapingInDatabase(
            classConfig.getClassroom().getId(),
            classConfig.getHourStart(), 
            classConfig.getHourEnd()
        );
            
        if(conflict) {
            throw new BadRequestException("Classroom configuration overlap found with another configuration in the database while saving");
        }
    }

    private void checkHourOverlapInDatabaseOnUpdate(ClassroomConfiguration classConfig){
        Boolean conflict = classroomConfigurationRepository.OverlapingInDatabaseOnUpdate(
            classConfig.getClassroom().getId(),
            classConfig.getHourStart(), 
            classConfig.getHourEnd(),
            classConfig.getId()
        );
            
        if(conflict) {
            throw new BadRequestException("Classroom configuration overlap found with another configuration in the database while updating");
        }

    }

    private void sameClassPeriodValidation(ClassroomConfiguration newClassConfig) {
        newClassConfig.getClassroom().getClassroomConfigurations().forEach(config -> {
            if (config.getClassPeriod().getId().equals(newClassConfig.getClassPeriod().getId()) && !newClassConfig.getClassPeriod().getName().equals("RECREO")) {
                throw new BadRequestException("A classroom configuration already exists with the same class period in the database");
            }
        });
    }

    private void sameStartOrEndHourValidation(ClassroomConfiguration classConfig) {
        ClassroomConfiguration found = classroomConfigurationRepository.findByClassroomIdAndHourStartOrClassroomIdAndHourEnd(classConfig.getClassroom().getId(), classConfig.getHourStart(), classConfig.getClassroom().getId(), classConfig.getHourEnd());
        
        if (found != null && !found.getId().equals(classConfig.getId())) {
            throw new BadRequestException("A classroom configuration already exists with the same hour start or the same hour end in the database");
        }
    }

}
