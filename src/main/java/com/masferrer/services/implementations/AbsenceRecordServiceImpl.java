package com.masferrer.services.implementations;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.masferrer.models.dtos.AbsenceRecordDTO;
import com.masferrer.models.dtos.AbsenceRecordWithStudentsDTO;
import com.masferrer.models.dtos.CreateAbsentRecordDTO;
import com.masferrer.models.dtos.EditAbsenceRecordDTO;
import com.masferrer.models.dtos.EditAbsentStudentDTO;
import com.masferrer.models.dtos.StudentAbsenceCountDTO;
import com.masferrer.models.entities.AbsenceRecord;
import com.masferrer.models.entities.AbsentStudent;
import com.masferrer.models.entities.Classroom;
import com.masferrer.models.entities.Code;
import com.masferrer.models.entities.Student;
import com.masferrer.models.entities.User;
import com.masferrer.repository.AbsenceRecordRepository;
import com.masferrer.repository.AbsentStudentRepository;
import com.masferrer.repository.ClassroomRepository;
import com.masferrer.repository.CodeRepository;
import com.masferrer.repository.StudentRepository;
import com.masferrer.services.AbsenceRecordService;
import com.masferrer.services.ClassroomService;
import com.masferrer.utils.EntityMapper;
import com.masferrer.utils.NotFoundException;

import jakarta.transaction.Transactional;

@Service
public class AbsenceRecordServiceImpl implements AbsenceRecordService{
    @Autowired
    private AbsenceRecordRepository absenceRecordRepository;

    @Autowired
    private ClassroomRepository classroomRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CodeRepository codeRepository;

    @Autowired
    private AbsentStudentRepository absentStudentRepository;

    @Autowired
    private ClassroomService classroomService;

    @Autowired
    private EntityMapper entityMapper;

    @Override
    public List<AbsenceRecordDTO> findAll() {

        List<AbsenceRecord> absenceRecords = absenceRecordRepository.findAll();
        if(absenceRecords.isEmpty()){
            return classroomService.findAll().stream()
                .map(classroom -> new AbsenceRecordDTO(LocalDate.now(), classroom, 0, 0))
                .collect(Collectors.toList());
        }
        List<AbsenceRecordDTO> response = absenceRecords.stream().map(entityMapper::map).collect(Collectors.toList());
        return response;

    }

    @Override
    public AbsenceRecord findById(UUID id) {
        return absenceRecordRepository.findById(id).orElseThrow(null);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public AbsenceRecordDTO createAbsenceRecord(CreateAbsentRecordDTO info) throws Exception {
        //encontrando el id del classroom
        Classroom idClassroom = classroomRepository.findById(info.getId_classroom()).orElse(null);
        if(idClassroom == null) {
            throw new NotFoundException("Classroom not found");
        }
        
        //buscando si ya existe un registro de asistencia para la fecha y el aula
        AbsenceRecord absenceRecordFound = absenceRecordRepository.findByDateAndClassroom(info.getDate(), idClassroom);
        if(absenceRecordFound != null) {
            throw new NotFoundException("Absence record already exists");
        }
        AbsenceRecord absenceRecord = new AbsenceRecord(info.getDate(), idClassroom, info.getMaleAttendance(), info.getFemaleAttendance());
        absenceRecord.setTeacherValidation(false);
        absenceRecord.setCoordinationValidation(false);
        absenceRecord = absenceRecordRepository.save(absenceRecord);

        //buscando el codigo
        Code code = codeRepository.findByDescription("Injustificada");
        //si el codigo no esta escrito como en la base lanzara la excepcion
        if(code == null) {
            throw new NotFoundException("Code not found");
        }
        //guardando los estudiantes ausentes
        //se usara final para que la referencia de absenceRecord no cambie, si se manda solo absenceRecord al llenar el constructor de AbsentStudent se pudre
        final AbsenceRecord finalAbsenceRecord = absenceRecord;
        //mapeando los absentStudentsDTO a absentStudents
        List<AbsentStudent> absentStudents = info.getAbsentStudents().stream().map(dto ->{
            //buscando al estudiante y codigo
            Student student = studentRepository.findById(dto.getId_student()).orElseThrow(()-> new NotFoundException("Student not found"));
            return new AbsentStudent(info.getDate(), student, code, finalAbsenceRecord, dto.getComments());
        }).collect(Collectors.toList());

        absentStudentRepository.saveAll(absentStudents);
        AbsenceRecordDTO response = entityMapper.map(absenceRecord);
        return response;
        
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public Boolean toggleTeacherValidation(UUID idAbsenceRecord) throws Exception {
        //encontrando el registro de asistencia
        AbsenceRecord absenceRecord = absenceRecordRepository.findById(idAbsenceRecord).orElse(null);
        if(absenceRecord == null){
            return false;
        }
        //haciendo que la validation de maestro sea true
        absenceRecord.setTeacherValidation(!absenceRecord.getTeacherValidation());
        absenceRecordRepository.save(absenceRecord);
        return true;
        
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public Boolean toggleCoordinationValidation(UUID idAbsenceRecord) throws Exception {
        //encontrando el registro de asistencia
        AbsenceRecord absenceRecord = absenceRecordRepository.findById(idAbsenceRecord).orElse(null);
        if(absenceRecord == null){
            return false;
        }
        //haciendo que la validation de coordinacion sea true
        absenceRecord.setCoordinationValidation(!absenceRecord.getCoordinationValidation());
        absenceRecordRepository.save(absenceRecord);
        return true;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public AbsenceRecordDTO editAbsenceRecord(EditAbsenceRecordDTO info, UUID id) throws Exception {
        // editando el absence record
        AbsenceRecord absenceRecord = absenceRecordRepository.findById(id).orElseThrow(() -> new NotFoundException("Absence record not found"));
        if (info.getDate() != null && info.getId_classroom() != null && info.getMaleAttendance() != null && info.getFemaleAttendance() != null) {
            absenceRecord.setDate(info.getDate());
            absenceRecord.setClassroom(classroomRepository.findById(info.getId_classroom()).orElseThrow(() -> new NotFoundException("Classroom not found")));
            absenceRecord.setMaleAttendance(info.getMaleAttendance());
            absenceRecord.setFemaleAttendance(info.getFemaleAttendance());
        }
        absenceRecord.setTeacherValidation(false);
        absenceRecord.setCoordinationValidation(false);

        // obtenemos la lista de los studiantes ausentes
        List<AbsentStudent> existingAbsentStudents = absenceRecord.getAbsentStudents();

        //creamos una lista de ids de los estudiantes ausentes (es una nueva lista)
        List<UUID> newStudentIds = info.getAbsentStudents().stream()
            .map(EditAbsentStudentDTO::getId_student)
            .collect(Collectors.toList());

        // eliminando estudiantes ausentes que ya no están en la nueva lista
        existingAbsentStudents.removeIf(absentStudent -> !newStudentIds.contains(absentStudent.getStudent().getId()));

        //actualizamos la lista de estudiantes ausentes (aqui es donde se actualizan los comentarios y los codigos)
        List<AbsentStudent> updatedAbsentStudents = info.getAbsentStudents().stream().map(dto -> {
            Student student = studentRepository.findById(dto.getId_student()).orElseThrow(() -> new NotFoundException("Student not found"));
            Code code = codeRepository.findById(dto.getId_code()).orElseThrow(() -> new NotFoundException("Code not found"));

            // si ya existe el estudiante en la lista de estudiantes ausentes, se actualizan los detalles del estudiantes (se crea otro porque el anterior se elimino)
            AbsentStudent absentStudent = existingAbsentStudents.stream()
                .filter(absstudent -> absstudent.getStudent().getId().equals(dto.getId_student()))
                .findFirst()
                .orElse(new AbsentStudent(info.getDate(), student, code, absenceRecord, dto.getComments()));

            //aqui solo actualizamos 
            absentStudent.setCode(code);
            absentStudent.setComments(dto.getComments());
            absentStudent.setDate(info.getDate());

            return absentStudent;
        }).collect(Collectors.toList());

        //guardando los cambios
        absenceRecord.setAbsentStudents(updatedAbsentStudents);
        absenceRecordRepository.save(absenceRecord);
        AbsenceRecordDTO response = entityMapper.map(absenceRecord);

        return response;
    }

    @Override
    public List<AbsenceRecordWithStudentsDTO> findByDate(LocalDate date) {
        List<AbsenceRecord> absenceRecords = absenceRecordRepository.findByDate(date);
        if (absenceRecords == null) {
            throw new NotFoundException("Absence record not found");
        }
        //inicializando manualmente a los absence student debido al LAZY
        absenceRecords.forEach(absence -> Hibernate.initialize(absence.getAbsentStudents()));

        //mapeando el absence record para que tenga un custom classroom
        List<AbsenceRecordWithStudentsDTO> response = entityMapper.mapToAbsenceRecordWithCustomClassroomDTOList(absenceRecords);

        return response.stream()
        .map(absence -> new AbsenceRecordWithStudentsDTO(
                absence.getId(), 
                absence.getDate(), 
                absence.getMaleAttendance(), 
                absence.getFemaleAttendance(), 
                absence.getTeacherValidation(), 
                absence.getCoordinationValidation(), 
                absence.getClassroom(), 
                absence.getAbsentStudents()))
        .collect(Collectors.toList());
    }

    
    @Override
    public List<AbsenceRecordDTO> findByDateNoStudent(LocalDate date) {
        List<AbsenceRecord> absenceRecords = absenceRecordRepository.findByDate(date);
        if(absenceRecords.isEmpty()){
            return classroomService.findAll().stream()
                .map(classroom -> new AbsenceRecordDTO(date, classroom, 0, 0))
                .collect(Collectors.toList());
        }

        List<AbsenceRecordDTO> response = absenceRecords.stream().map(entityMapper::map).collect(Collectors.toList());
        return response;
    }


    @Override
    public List<AbsenceRecordDTO> findByMonthAndYear(int month, int year) {
        List<AbsenceRecord> absenceRecords = absenceRecordRepository.findByMonthAndYear(month, year);
        List<AbsenceRecordDTO> response = absenceRecords.stream().map(entityMapper::map).collect(Collectors.toList());
        return response;
    }

    @Override
    public List<AbsenceRecordDTO> findByClassroom(UUID idClassroom) {
        Classroom classroom = classroomRepository.findById(idClassroom).orElseThrow(() -> new NotFoundException("Classroom not found"));
        List<AbsenceRecord> absenceRecords = absenceRecordRepository.findByClassroom(classroom);

        List<AbsenceRecordDTO> response = absenceRecords.stream().map(entityMapper::map).collect(Collectors.toList());
        return response;
    }

    @Override
    public AbsenceRecordDTO findByDateAndClassroom(LocalDate date, UUID idClassrooms) {
        Classroom classroom = classroomRepository.findById(idClassrooms).orElseThrow(() -> new NotFoundException("Classroom not found"));
        AbsenceRecord absenceRecord = absenceRecordRepository.findByDateAndClassroom(date, classroom);
        
        //mappeando a absence recorddto
        AbsenceRecordDTO response = entityMapper.map(absenceRecord);
        return response;

    }

    @Override
    public List<AbsenceRecordWithStudentsDTO> findByClassroomAndShift(UUID idClassroom, UUID shiftId) {
        Classroom classroom = classroomRepository.findById(idClassroom).orElseThrow(() -> new NotFoundException("Classroom not found"));
        
        
        List<AbsenceRecord> absenceRecord = absenceRecordRepository.findByClassroomAndShift(classroom.getId(), shiftId);
        List<AbsenceRecordWithStudentsDTO> response = entityMapper.mapToAbsenceRecordWithCustomClassroomDTOList(absenceRecord);

        if (absenceRecord == null || absenceRecord.isEmpty()) {
            throw new NotFoundException("Absence records not found for the classroom and shift");
        }

        //cargando manualmente los absentStudents
        response.forEach(absence -> Hibernate.initialize(absence.getAbsentStudents()));

        return response.stream()
        .map(absence -> new AbsenceRecordWithStudentsDTO(
                absence.getId(), 
                absence.getDate(), 
                absence.getMaleAttendance(), 
                absence.getFemaleAttendance(), 
                absence.getTeacherValidation(), 
                absence.getCoordinationValidation(), 
                absence.getClassroom(), 
                absence.getAbsentStudents()))
        .collect(Collectors.toList());
    }

    @Override
    public List<AbsenceRecord> findByClassroomAndShiftAndYear(UUID idClassroom, UUID idShift, int year) {
        Classroom classroom = classroomRepository.findById(idClassroom).orElseThrow(() -> new NotFoundException("Classroom not found"));
        return absenceRecordRepository.findByClassroomAndShiftAndYear(classroom, idShift, year);
    }

    @Override
    public List<AbsenceRecordDTO> findByDateAndShift(LocalDate date, UUID idShift) {
        List<AbsenceRecord> absenceRecords = absenceRecordRepository.findByDateAndShift(date, idShift);
        if (absenceRecords == null) {
            throw new NotFoundException("Absence record not found");
        }
        List<AbsenceRecordDTO> response = absenceRecords.stream().map(entityMapper::map).collect(Collectors.toList());
        return response;
    }

    @Override
    public List<AbsenceRecordDTO> findByUserAndDate(UUID userId, LocalDate date) {
        //obtener al usuario desde el token
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<AbsenceRecord> absenceRecords = absenceRecordRepository.findByUserAndDate(user.getId(), date);
        List<AbsenceRecordDTO> response = absenceRecords.stream().map(entityMapper::map).collect(Collectors.toList());
        return response;
    }

    @Override
    public List<StudentAbsenceCountDTO> getTopAbsentStudentsByClassroom(UUID classroomId, String year) {
        Pageable pageable = PageRequest.of(0, 2); // muestra solo los 2 primeros estudiantes con más faltas
        List<Object[]> results = absentStudentRepository.findTopAbsentStudentsByClassroom(classroomId, year, pageable);
        
        return results.stream()
            .map(result -> {
                Student student = (Student) result[0];
                Long unjustifiedAbsences = (Long) result[1];
                Long justifiedAbsences = (Long) result[2];
                Long totalAbsences = unjustifiedAbsences + justifiedAbsences; 
                
                return new StudentAbsenceCountDTO(
                    student, 
                    unjustifiedAbsences, 
                    justifiedAbsences, 
                    totalAbsences 
                );
            })
            .collect(Collectors.toList());
    }

    @Override
    public List<StudentAbsenceCountDTO> getAbsentStudentsCountByClassroom(UUID classroomId, String year) {
        List<Object[]> results = absentStudentRepository.findAllAbsentStudentByClassroomWithAbsenceType(classroomId, year);
    
        return results.stream()
            .map(result -> {
                Long unjustifiedAbsences = (Long) result[1];
                Long justifiedAbsences = (Long) result[2];
                Long totalAbsences = unjustifiedAbsences + justifiedAbsences;
    
                return new StudentAbsenceCountDTO(
                    (Student) result[0], 
                    unjustifiedAbsences, 
                    justifiedAbsences, 
                    totalAbsences
                );
            })
            .collect(Collectors.toList());
    }

    @Override
    public List<StudentAbsenceCountDTO> getTopAbsenceStudentsCountByUserAndShift(UUID userId, UUID shift, String year) {
        List<Classroom> classrooms = classroomService.getClassroomsByUser(userId);
    List<UUID> classroomIds = classrooms.stream().map(Classroom::getId).collect(Collectors.toList());
    Pageable pageable = PageRequest.of(0, 2);
    
    List<Object[]> results = absentStudentRepository.findTopAbsentStudentsByClassroomsAndShiftAndYear(classroomIds, shift, year, pageable);
    
    return results.stream()
        .map(result -> {
            Long unjustifiedAbsences = (Long) result[1];
            Long justifiedAbsences = (Long) result[2];
            Long totalAbsences = unjustifiedAbsences + justifiedAbsences;
    
            return new StudentAbsenceCountDTO(
                (Student) result[0], 
                unjustifiedAbsences, 
                justifiedAbsences, 
                totalAbsences
            );
        })
        .collect(Collectors.toList());
    }

    @Override
    public List<StudentAbsenceCountDTO> getAllAbsenceStudentByUserAndShift(UUID userId, UUID shift, String year) {
        List<Classroom> classrooms = classroomService.getClassroomsByUser(userId);
        List<UUID> classroomIds = classrooms.stream().map(Classroom::getId).collect(Collectors.toList());
        
        List<Object[]> results = absentStudentRepository.findAllAbsentStudentsByClassroomsAndShiftAndYear(classroomIds, shift, year);
        
        return results.stream()
            .map(result -> {
                Long unjustifiedAbsences = (Long) result[1];
                Long justifiedAbsences = (Long) result[2];
                Long totalAbsences = unjustifiedAbsences + justifiedAbsences;
        
                return new StudentAbsenceCountDTO(
                    (Student) result[0], 
                    unjustifiedAbsences, 
                    justifiedAbsences, 
                    totalAbsences
                );
            })
            .collect(Collectors.toList());
    }


}
