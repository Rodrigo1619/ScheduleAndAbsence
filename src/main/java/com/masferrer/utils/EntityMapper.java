package com.masferrer.utils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.masferrer.models.dtos.CustomClassroomDTO;
import com.masferrer.models.dtos.ScheduleDTO;
import com.masferrer.models.dtos.ScheduleListDTO;
import com.masferrer.models.dtos.ShortClassroomConfigurationDTO;
import com.masferrer.models.dtos.ShortUserDTO;
import com.masferrer.models.dtos.ShowGradeConcatDTO;
import com.masferrer.models.dtos.StudentXClassroomDTO;
import com.masferrer.models.dtos.UserDTO;
import com.masferrer.models.dtos.UserXSubjectDTO;
import com.masferrer.models.entities.AbsenceRecord;
import com.masferrer.models.entities.AbsentStudent;
import com.masferrer.models.entities.Classroom;
import com.masferrer.models.entities.ClassroomConfiguration;
import com.masferrer.models.entities.Grade;
import com.masferrer.models.entities.Schedule;
import com.masferrer.models.entities.Student_X_Classroom;
import com.masferrer.models.entities.User;
import com.masferrer.models.entities.User_X_Subject;
import com.masferrer.models.dtos.AbsenceRecordDTO;
import com.masferrer.models.dtos.AbsenceRecordWithStudentsDTO;
import com.masferrer.models.dtos.ClassConfigurationDTO;
import com.masferrer.models.dtos.ClassroomConfigurationListDTO;

@Component
public class EntityMapper {

    public UserDTO mapUser(User user) {
        UserDTO showUsersAdminDTO = new UserDTO();
        showUsersAdminDTO.setId(user.getId());
        showUsersAdminDTO.setName(user.getName());
        showUsersAdminDTO.setEmail(user.getEmail());
        showUsersAdminDTO.setRole(user.getRole());
        showUsersAdminDTO.setActive(user.getActive());
        showUsersAdminDTO.setVerifiedEmail(user.getVerifiedEmail());
        return showUsersAdminDTO;
    }

    public ShortUserDTO map(User user) {
        ShortUserDTO showUserDTO = new ShortUserDTO(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getActive(),
            user.getVerifiedEmail()
        );
        return showUserDTO;
    }

    public List<UserDTO> mapToUserDTO(List<User> users) {
        return users.stream().map(user -> mapUser(user)).toList();
    }

    public List<ShortUserDTO> mapToShortUserDTO(List<User> users) {
        return users.stream().map(user -> map(user)).toList();
    }

    public CustomClassroomDTO map(Classroom classroom) {
        CustomClassroomDTO customClassroomDTO = new CustomClassroomDTO();
        customClassroomDTO.setId(classroom.getId());
        customClassroomDTO.setYear(classroom.getYear());
        customClassroomDTO.setGrade(mapGradeConcatDTO(classroom.getGrade()));
        customClassroomDTO.setHomeroomTeacher(map(classroom.getUser()));
        return customClassroomDTO;
    }

    public List<CustomClassroomDTO> mapClassrooms(List<Classroom> classrooms) {
        return classrooms.stream().map(classroom -> map(classroom)).toList();
    }

    public ClassConfigurationDTO map(ClassroomConfiguration classroomConfiguration) {
        ClassConfigurationDTO classroomConfigurationDTO = new ClassConfigurationDTO();
        classroomConfigurationDTO.setId(classroomConfiguration.getId());
        classroomConfigurationDTO.setHourStart(classroomConfiguration.getHourStart());
        classroomConfigurationDTO.setHourEnd(classroomConfiguration.getHourEnd());
        classroomConfigurationDTO.setClassPeriod(classroomConfiguration.getClassPeriod());
        classroomConfigurationDTO.setClassroom(map(classroomConfiguration.getClassroom()));
        return classroomConfigurationDTO;
    }

    public List<ClassroomConfigurationListDTO> mapToClassroomConfigurationsListDTO(List<ClassroomConfiguration> classroomConfigurations) {
        // Group by classroom
        Map<Classroom, List<ClassroomConfiguration>> groupedByClassroom = classroomConfigurations.stream()
            .collect(Collectors.groupingBy(ClassroomConfiguration::getClassroom));

        // Map each group to a ClassroomConfigurationListDTO
        return groupedByClassroom.entrySet().stream()
            .map(entry -> {
                Classroom classroom = entry.getKey();
                List<ClassroomConfiguration> configurations = entry.getValue();

                CustomClassroomDTO classroomDTO = map(classroom);
                
                List<ShortClassroomConfigurationDTO> configurationDTOs = configurations.stream()
                    .map(config -> new ShortClassroomConfigurationDTO(config.getId(), config.getHourStart(), config.getHourEnd(), config.getClassPeriod()))
                    .collect(Collectors.toList());

                return new ClassroomConfigurationListDTO(classroomDTO, configurationDTOs);
            })
            .collect(Collectors.toList());
    }

    public UserXSubjectDTO map(User_X_Subject userXSubject) {
        UserXSubjectDTO userXSubjectDTO = new UserXSubjectDTO();
        userXSubjectDTO.setId(userXSubject.getId());
        userXSubjectDTO.setTeacher(map(userXSubject.getUser()));
        userXSubjectDTO.setSubject(userXSubject.getSubject());
        return userXSubjectDTO;
    }

    public List<UserXSubjectDTO> mapUserXSubjectList(List<User_X_Subject> userXSubjects) {
        return userXSubjects.stream().map(userXSubject -> map(userXSubject)).toList();
    }

    public ScheduleDTO<ShortClassroomConfigurationDTO> map(Schedule schedule) {
        ScheduleDTO<ShortClassroomConfigurationDTO> scheduleDTO = new ScheduleDTO<>();
        scheduleDTO.setId(schedule.getId());
        scheduleDTO.setUser_x_subject(map(schedule.getUser_x_subject()));
        scheduleDTO.setClassroomConfiguration(new ShortClassroomConfigurationDTO(
            schedule.getClassroomConfiguration().getId(),
            schedule.getClassroomConfiguration().getHourStart(),
            schedule.getClassroomConfiguration().getHourEnd(),
            schedule.getClassroomConfiguration().getClassPeriod()
        ));
        scheduleDTO.setWeekday(schedule.getWeekday());
        return scheduleDTO;
    }

    public ScheduleDTO<ClassConfigurationDTO> mapToScheduleDTO(Schedule schedule) {
        ScheduleDTO<ClassConfigurationDTO> scheduleDTO = new ScheduleDTO<>();
        scheduleDTO.setId(schedule.getId());
        scheduleDTO.setUser_x_subject(map(schedule.getUser_x_subject()));
        scheduleDTO.setClassroomConfiguration(map(schedule.getClassroomConfiguration()));
        scheduleDTO.setWeekday(schedule.getWeekday());
        return scheduleDTO;
    }

    public List<ScheduleListDTO> mapToSchedulesListDTO(List<Schedule> schedules) {
        // Group by classroom
        Map<Classroom, List<Schedule>> groupedByClassroom = schedules.stream()
            .collect(Collectors.groupingBy(schedule -> schedule.getClassroomConfiguration().getClassroom()));

        // Map each group to a ScheduleListDTO
        return groupedByClassroom.entrySet().stream()
            .map(entry -> {
                Classroom classroom = entry.getKey();
                List<Schedule> classroomSchedules = entry.getValue();

                CustomClassroomDTO classroomDTO = map(classroom);
                
                List<ScheduleDTO<ShortClassroomConfigurationDTO>> scheduleDTOs = classroomSchedules.stream()
                    .map(this::map)
                    .collect(Collectors.toList());

                return new ScheduleListDTO(classroomDTO, scheduleDTOs);
            })
            .collect(Collectors.toList());
    }

    public StudentXClassroomDTO map(Student_X_Classroom student_X_Classroom) {
        StudentXClassroomDTO studentXClassroomDTO = new StudentXClassroomDTO();
        studentXClassroomDTO.setId(student_X_Classroom.getId());
        studentXClassroomDTO.setStudent(student_X_Classroom.getStudent());
        studentXClassroomDTO.setClassroom(map(student_X_Classroom.getClassroom()));
        studentXClassroomDTO.setAssigned(student_X_Classroom.getAssigned());
        return studentXClassroomDTO;
    }

    public List<StudentXClassroomDTO> mapStudentXClassroomList(List<Student_X_Classroom> student_X_Classrooms) {
        return student_X_Classrooms.stream().map(student_X_Classroom -> map(student_X_Classroom)).toList();
    }

    public ShowGradeConcatDTO mapGradeConcatDTO(Grade grade){
        String concatName = grade.getName() + " " + grade.getSection();
        ShowGradeConcatDTO showGradeConcatDTO = new ShowGradeConcatDTO(
            grade.getId(),
            concatName,
            grade.getIdGoverment(),
            grade.getSection(),
            grade.getShift()
        );
        return showGradeConcatDTO;
    }

    public AbsenceRecordDTO map(AbsenceRecord absenceRecord) {
        AbsenceRecordDTO absenceRecordDTO = new AbsenceRecordDTO();
        absenceRecordDTO.setId(absenceRecord.getId());
        absenceRecordDTO.setDate(absenceRecord.getDate());
        absenceRecordDTO.setMaleAttendance(absenceRecord.getMaleAttendance());
        absenceRecordDTO.setFemaleAttendance(absenceRecord.getFemaleAttendance());
        absenceRecordDTO.setTeacherValidation(absenceRecord.getTeacherValidation());
        absenceRecordDTO.setCoordinationValidation(absenceRecord.getCoordinationValidation());
        absenceRecordDTO.setClassroom(map(absenceRecord.getClassroom()));
        return absenceRecordDTO;
    }

    public AbsenceRecordWithStudentsDTO mapToAbsenceRecordWithCustomClassroomDTO(AbsenceRecord absenceRecord) {
        CustomClassroomDTO customClassroomDTO = map(absenceRecord.getClassroom());
        return new AbsenceRecordWithStudentsDTO(
            absenceRecord.getId(),
            absenceRecord.getDate(),
            absenceRecord.getMaleAttendance(),
            absenceRecord.getFemaleAttendance(),
            absenceRecord.getTeacherValidation(),
            absenceRecord.getCoordinationValidation(),
            customClassroomDTO,
            absenceRecord.getAbsentStudents()
        );
    }

    public AbsenceRecordWithStudentsDTO mapToAbsenceRecordWithAbsentStudentList(AbsenceRecord absenceRecord, List<AbsentStudent> absentStudents) {
        CustomClassroomDTO customClassroomDTO = map(absenceRecord.getClassroom());
        return new AbsenceRecordWithStudentsDTO(
            absenceRecord.getId(),
            absenceRecord.getDate(),
            absenceRecord.getMaleAttendance(),
            absenceRecord.getFemaleAttendance(),
            absenceRecord.getTeacherValidation(),
            absenceRecord.getCoordinationValidation(),
            customClassroomDTO,
            absentStudents
        );
    }

    public List<AbsenceRecordWithStudentsDTO> mapToAbsenceRecordWithCustomClassroomDTOList(List<AbsenceRecord> absenceRecord) {
        return absenceRecord.stream()
            .map(this::mapToAbsenceRecordWithCustomClassroomDTO)
            .collect(Collectors.toList());
    }
}
