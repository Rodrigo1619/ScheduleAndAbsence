package com.masferrer.services.implementations;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.masferrer.models.dtos.ClassConfigurationDTO;
import com.masferrer.models.dtos.CreateScheduleListDTO;
import com.masferrer.models.dtos.ScheduleDTO;
import com.masferrer.models.dtos.ScheduleListDTO;
import com.masferrer.models.dtos.UpdateScheduleDTO;
import com.masferrer.models.dtos.UpdateScheduleListDTO;
import com.masferrer.models.entities.Classroom;
import com.masferrer.models.entities.ClassroomConfiguration;
import com.masferrer.models.entities.Schedule;
import com.masferrer.models.entities.Subject;
import com.masferrer.models.entities.User;
import com.masferrer.models.entities.User_X_Subject;
import com.masferrer.models.entities.Weekday;
import com.masferrer.repository.ClassPeriodRepository;
import com.masferrer.repository.ClassroomConfigurationRepository;
import com.masferrer.repository.ClassroomRepository;
import com.masferrer.repository.ScheduleRepository;
import com.masferrer.repository.ShiftRepository;
import com.masferrer.repository.SubjectRepository;
import com.masferrer.repository.UserRepository;
import com.masferrer.repository.User_X_SubjectRepository;
import com.masferrer.repository.WeekDayRepository;
import com.masferrer.services.ScheduleService;
import com.masferrer.utils.BadRequestException;
import com.masferrer.utils.EntityMapper;
import com.masferrer.utils.ExistExceptions;
import com.masferrer.utils.NotFoundException;

import jakarta.transaction.Transactional;

@Service
public class ScheduleServiceImpl implements ScheduleService{
    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private User_X_SubjectRepository userXSubjectRepository;

    @Autowired
    private WeekDayRepository weekDayRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private ShiftRepository shiftRepository;

    @Autowired
    private ClassroomConfigurationRepository classConfigurationRepository;

    @Autowired
    private ClassroomRepository classroomRepository;

    @Autowired
    private ClassPeriodRepository classPeriodRepository;

    @Autowired
    private EntityMapper entityMapper;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public List<ScheduleListDTO> createSchedule(CreateScheduleListDTO createScheduleListDTO) {
    
        List<Schedule> schedules = createScheduleListDTO.getSchedules().stream()
        .map(dto -> {
                UUID userId = dto.getId_user();
                UUID subjectId = dto.getId_subject();
                UUID weekdayId = dto.getId_weekday();

                User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
                Subject subject = subjectRepository.findById(subjectId).orElseThrow(() -> new NotFoundException("Subject not found"));
                
                //esto es para poder encontrar al usuario con la materia 
                User_X_Subject assignedSubject = userXSubjectRepository.findByUserAndSubject(user, subject);
                if (assignedSubject == null) {
                    throw new IllegalArgumentException("User is not assigned to the provided subject");
                } 

                ClassroomConfiguration classConfiguration = classConfigurationRepository.findById(dto.getId_classroomConfiguration())
                    .orElseThrow(() -> new NotFoundException("Classroom configuration not found"));

                Weekday weekday = weekDayRepository.findById(weekdayId).orElseThrow(() -> new NotFoundException("Weekday not found"));
    
                return new Schedule(
                    assignedSubject,
                    classConfiguration,
                    weekday
                );
            })
            .collect(Collectors.toList());
        
        validateSchedules(schedules);

        List<Schedule> savedSchedules = scheduleRepository.saveAll(schedules);

        return entityMapper.mapToSchedulesListDTO(savedSchedules);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public List<ScheduleListDTO> updateSchedule(UpdateScheduleListDTO schedules) throws Exception {
        // Delete the schedules in the deleteList
        if (schedules.getDeleteList() != null && !schedules.getDeleteList().isEmpty()) {
            deleteSchedule(schedules.getDeleteList());
        }

        // Create new schedules
        if (schedules.getNewSchedules() != null && !schedules.getNewSchedules().isEmpty()) {
            CreateScheduleListDTO createScheduleListDTO = new CreateScheduleListDTO();
            createScheduleListDTO.setSchedules(schedules.getNewSchedules().stream()
                .map(dto -> new CreateScheduleListDTO.ScheduleDTO(
                    dto.getId_user(),
                    dto.getId_subject(),
                    dto.getId_classroomConfiguration(),
                    dto.getId_weekday()
                ))
                .collect(Collectors.toList()));

            return createSchedule(createScheduleListDTO);
        } else {
            throw new IllegalArgumentException("No schedules provided to update");
        }
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public List<Schedule> updateSchedule(List<UpdateScheduleDTO> schedules) {

        List<Schedule> schedulesList = schedules.stream()
            .map(dto -> {
                Schedule scheduleToUpdate = scheduleRepository.findById(dto.getId_schedule())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid schedule Id"));

                // Si el usuario o la materia son diferentes a los que ya tiene asignados, se actualizan, si no, se mantienen los mismos
                if (dto.getId_user() != scheduleToUpdate.getUser_x_subject().getUser().getId() || 
                dto.getId_subject() != scheduleToUpdate.getUser_x_subject().getSubject().getId()) {
                    
                    User user = userRepository.findById(dto.getId_user()).orElseThrow(() -> new IllegalArgumentException("Invalid user Id"));
                    Subject subject = subjectRepository.findById(dto.getId_subject()).orElseThrow(() -> new IllegalArgumentException("Invalid subject Id"));
                    User_X_Subject assignedSubject = userXSubjectRepository.findByUserAndSubject(user, subject);
                    if (assignedSubject == null) {
                        throw new NotFoundException("User is not assigned to the provided subject");
                    }
                    scheduleToUpdate.setUser_x_subject(assignedSubject);
                } else {
                    scheduleToUpdate.setUser_x_subject(scheduleToUpdate.getUser_x_subject());
                }

                // Si la configuracion del aula es diferente a la que ya tiene asignada, se actualiza, si no, se mantiene la misma
                if (dto.getId_classroomConfiguration() != scheduleToUpdate.getClassroomConfiguration().getId()) {
                    ClassroomConfiguration classConfiguration = classConfigurationRepository.findById(dto.getId_classroomConfiguration())
                        .orElseThrow(() -> new IllegalArgumentException("Invalid classroom configuration Id"));
                    scheduleToUpdate.setClassroomConfiguration(classConfiguration);
                } else {
                    scheduleToUpdate.setClassroomConfiguration(scheduleToUpdate.getClassroomConfiguration());
                }
                // Si el dia de la semana es diferente al que ya tiene asignado, lanza error y no se actualiza
                //si no, se mantiene el mismo
                if (!dto.getId_weekday().equals(scheduleToUpdate.getWeekday().getId())) {
                    throw new IllegalArgumentException("Weekday cannot be updated");
                } else {
                    scheduleToUpdate.setWeekday(scheduleToUpdate.getWeekday());
                }
                
               // viendo si no existe el conflicto que ya existe en otro horario
                boolean conflictExists = scheduleRepository.existsConflictingScheduleForClassroom(
                    scheduleToUpdate.getClassroomConfiguration().getClassroom().getId(), 
                    scheduleToUpdate.getWeekday().getId(), 
                    scheduleToUpdate.getUser_x_subject().getId(), 
                    scheduleToUpdate.getClassroomConfiguration().getHourStart(),
                    scheduleToUpdate.getClassroomConfiguration().getHourEnd());

                if (conflictExists) {
                    throw new IllegalArgumentException("Schedule conflict");
                }

                return scheduleToUpdate;
            })
            .collect(Collectors.toList());

        return scheduleRepository.saveAll(schedulesList);
    }
    
    private void validateSchedules(List<Schedule> schedules) {
        for (Schedule schedule : schedules) {
            //vemos si los parametros del horario existen, si es asi existe un overlap
            boolean existsInDb = scheduleRepository.existsOverlappingSchedule(
                schedule.getUser_x_subject().getUser().getId(),
                schedule.getWeekday().getId(), 
                schedule.getClassroomConfiguration().getClassroom().getYear(),
                schedule.getClassroomConfiguration().getHourStart(), 
                schedule.getClassroomConfiguration().getHourEnd()
            );

            if (existsInDb) {
                throw new ExistExceptions("Overlapping schedule exists: this schedule is already used. " +
                "UserXSubject: " + schedule.getUser_x_subject().getUser().getName() + " - " + schedule.getUser_x_subject().getSubject().getName() + 
                " ClassroomConfig: " + schedule.getClassroomConfiguration().getId() +
                " Weekday: " + schedule.getWeekday().getDay());
            }
            //ver si existe un conflicto con otros horarios, es decir ver si ya existe un profesor dando clases en una aula a la misma hora
            boolean existsConflicting = scheduleRepository.existsConflictingScheduleForClassroom(
                schedule.getClassroomConfiguration().getClassroom().getId(),
                schedule.getWeekday().getId(),
                schedule.getUser_x_subject().getUser().getId(),
                schedule.getClassroomConfiguration().getHourStart(),
                schedule.getClassroomConfiguration().getHourEnd()
            );

            if (existsConflicting) {
                throw new ExistExceptions("Error: this classroom in this schedule is already used by another teacher." + 
                " Classroom:" + schedule.getClassroomConfiguration().getClassroom().getId() + 
                " Classconfiguration:" + schedule.getClassroomConfiguration().getId() +
                " Weekday:" + schedule.getWeekday().getId() + 
                " UserXSubject:" + schedule.getUser_x_subject().getUser().getId() + 
                " " + schedule.getClassroomConfiguration().getHourStart() + 
                " " + schedule.getClassroomConfiguration().getHourEnd());
            }
            // vemos cada horario para ver si no existe un conflicto con otro horario
            for (Schedule otherSchedule : schedules) {
                if (!schedule.equals(otherSchedule) &&
                    schedule.getUser_x_subject().equals(otherSchedule.getUser_x_subject()) &&
                    schedule.getWeekday().equals(otherSchedule.getWeekday()) &&
                    isOverlapping(schedule.getClassroomConfiguration().getHourStart(), schedule.getClassroomConfiguration().getHourEnd(), 
                    otherSchedule.getClassroomConfiguration().getHourStart(), otherSchedule.getClassroomConfiguration().getHourEnd())) {
                        throw new ExistExceptions("Overlapping schedules for the same user and weekday within the provided list.");
                }
            }
        }
    }
    //verificacion extra para ver si el horario no se sobrepone
    private boolean isOverlapping(LocalTime start1, LocalTime end1, LocalTime start2, LocalTime end2) {
        return (start1.isBefore(end2) && start2.isBefore(end1));
    }

    @Override
    public List<ScheduleListDTO> getSchedulesByUserIdAndYear(UUID userId, int year) {
        if(!userRepository.existsById(userId)){
            throw new NotFoundException("User not found");
        }
        List<Schedule> schedules = scheduleRepository.findSchedulesByUserIdAndYear(userId, year);
        if(schedules == null || schedules.isEmpty()){
            return null;
        }
        return entityMapper.mapToSchedulesListDTO(schedules);
    }

    @Override
    public List<ScheduleListDTO> getSchedulesByUserIdAndShiftAndYear(UUID userId, UUID shiftId, String year){
        if(year == null || year.isEmpty()){
            throw new IllegalArgumentException("year is required");
        }
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found");
        }
        if(!shiftRepository.existsById(shiftId)){
            throw new NotFoundException("Shift not found");
        }
        List<Schedule> schedules = scheduleRepository.findSchedulesByUserAndShiftAndYear(userId, shiftId, year);
        if(schedules == null || schedules.isEmpty()){
            return null;
        }
        return entityMapper.mapToSchedulesListDTO(schedules);
    }

    @Override
    public List<ScheduleListDTO> getSchedulesByUserTokenAndShiftAndYear(UUID shiftId, String year) {
        if(year == null || year.isEmpty()){
            throw new BadRequestException("year is required");
        }
        if(!shiftRepository.existsById(shiftId)){
            throw new NotFoundException("Shift not found");
        } 
        //obteniendo la informacion del usuario y despues el id
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UUID userId = user.getId();

        List<Schedule> schedules = scheduleRepository.findSchedulesByUserAndShiftAndYear(userId, shiftId, year);
        if(schedules == null || schedules.isEmpty()){
            return null;
        }

        return entityMapper.mapToSchedulesListDTO(schedules);
    }

    @Override
    public List<ScheduleListDTO> getScheduleByClassroomId(UUID classroomId) {
        Classroom foundClassroom = classroomRepository.findById(classroomId).orElseThrow(() -> new NotFoundException("Classroom not found"));

        List<Schedule> schedules = scheduleRepository.findSchedulesByClassroomId(foundClassroom.getId());

        return entityMapper.mapToSchedulesListDTO(schedules);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void deleteSchedule(List<UUID> schedulesIds) throws Exception {
        List<Schedule> schedules = scheduleRepository.findAllById(schedulesIds);
        if (schedules.size() != schedulesIds.size()) {
            throw new IllegalArgumentException("Invalid schedule Id");
        }
        scheduleRepository.deleteAll(schedules);
    }

    @Override
    public List<ScheduleListDTO> findAll() {
        List<Schedule> schedules = scheduleRepository.findAll();

        return entityMapper.mapToSchedulesListDTO(schedules);
    }

    @Override
    public ScheduleDTO<ClassConfigurationDTO> findScheduleByParameters(UUID classperiodId, UUID shiftId, UUID weekdayId, String year, UUID userId,UUID classroomId) {

        // Check existence of classPeriod, shift, and weekday
        if (!classPeriodRepository.existsById(classperiodId)) {
            throw new NotFoundException("Classperiod not found");
        }

        if (!shiftRepository.existsById(shiftId)) {
            throw new NotFoundException("Shift not found");
        }

        if (!weekDayRepository.existsById(weekdayId)) {
            throw new NotFoundException("Weekday not found");
        }

        if(year == null || year.isEmpty()){
            throw new IllegalArgumentException("year is required");
        }

        Schedule schedule = null;

        if(userId != null && classroomId == null){
            if (!userRepository.existsById(userId)) {
                throw new NotFoundException("User not found");
            }

            List<Schedule> schedules = scheduleRepository.findByClassPeriodShiftWeekdayYearUserId(
                classperiodId, shiftId, weekdayId, year, userId);

            if (schedules.isEmpty()) {
                return null;
            }

            schedule = schedules.get(0);

        } else if(userId == null && classroomId != null){
            if (!classroomRepository.existsById(classroomId)) {
                throw new NotFoundException("Classroom not found");
            }

            List<Schedule> schedules = scheduleRepository.findByClassPeriodShiftWeekdayYearClassroomId(
                classperiodId, shiftId, weekdayId, year, classroomId);

            if (schedules.isEmpty()) {
                return null;
            }

            schedule = schedules.get(0);

        } else {
            throw new IllegalArgumentException("Only one of userId or classroomId must be provided");
        }

        // Map schedule found to ScheduleDTO
        ScheduleDTO<ClassConfigurationDTO> scheduleDTO = entityMapper.mapToScheduleDTO(schedule);

        return scheduleDTO;
    }
    
}