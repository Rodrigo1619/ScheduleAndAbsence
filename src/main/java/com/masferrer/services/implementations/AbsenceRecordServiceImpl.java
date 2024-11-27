package com.masferrer.services.implementations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.masferrer.models.dtos.AbsenceRecordDTO;
import com.masferrer.models.dtos.AbsenceRecordWithStudentsAttendanceDTO;
import com.masferrer.models.dtos.AbsenceRecordWithStudentsDTO;
import com.masferrer.models.dtos.CreateAbsentRecordDTO;
import com.masferrer.models.dtos.EditAbsenceRecordDTO;
import com.masferrer.models.dtos.StudentAbsenceCountDTO;
import com.masferrer.models.dtos.StudentAttendanceDTO;
import com.masferrer.models.dtos.TopStudentsByShiftDTO;
import com.masferrer.models.dtos.TopStudentsByShiftDTO.TopStudentAbsenceDTO;
import com.masferrer.models.entities.AbsenceRecord;
import com.masferrer.models.entities.AbsentStudent;
import com.masferrer.models.entities.Classroom;
import com.masferrer.models.entities.Code;
import com.masferrer.models.entities.Shift;
import com.masferrer.models.entities.Student;
import com.masferrer.models.entities.User;
import com.masferrer.repository.AbsenceRecordRepository;
import com.masferrer.repository.AbsentStudentRepository;
import com.masferrer.repository.ClassroomRepository;
import com.masferrer.repository.CodeRepository;
import com.masferrer.repository.ShiftRepository;
import com.masferrer.repository.StudentRepository;
import com.masferrer.repository.StudentXClassroomRepository;
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
    private ShiftRepository shiftRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CodeRepository codeRepository;

    @Autowired
    private AbsentStudentRepository absentStudentRepository;

    @Autowired
    private ClassroomService classroomService;
    
    @Autowired
    private StudentXClassroomRepository studentXClassroomRepository;

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
        Code code = codeRepository.findByDescription("Otro, No justificado");
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
    public void deleteAbsenceStudents(List<UUID> absentStudentsIds) throws Exception{
        List<AbsentStudent> absentStudents = absentStudentRepository.findAllById(absentStudentsIds);
        if(absentStudents.size() != absentStudentsIds.size()){
            throw new IllegalArgumentException("Invalid absent student id");
        }
        absentStudentRepository.deleteAll(absentStudents);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public AbsenceRecordWithStudentsDTO editAbsenceRecord(EditAbsenceRecordDTO info, UUID id) throws Exception {
        if(info.getDeleteAbsentStudents() != null && !info.getDeleteAbsentStudents().isEmpty()){
            deleteAbsenceStudents(info.getDeleteAbsentStudents());
        }

        // editando el absence record
        AbsenceRecord absenceRecord = absenceRecordRepository.findById(id).orElseThrow(() -> new NotFoundException("Absence record not found"));
        if(info.getMaleAttendance() != null && !info.getMaleAttendance().equals(absenceRecord.getMaleAttendance())){
            absenceRecord.setMaleAttendance(info.getMaleAttendance());
        }
        if(info.getFemaleAttendance() != null && !info.getFemaleAttendance().equals(absenceRecord.getFemaleAttendance())){
            absenceRecord.setFemaleAttendance(info.getFemaleAttendance());
        }

        List<AbsentStudent> savedAbsentStudents = new ArrayList<>();

        if(info.getAbsentStudents()!=null && !info.getAbsentStudents().isEmpty()){
            // Obtenemos la lista de los studiantes ausentes actuales
            List<AbsentStudent> existingAbsentStudents = absenceRecord.getAbsentStudents();

            //Actualizamos la lista de estudiantes ausentes del absence record
            List<AbsentStudent> updatedAbsentStudents = info.getAbsentStudents().stream().map(dto -> {
                // si ya existe el estudiante en la lista de estudiantes ausentes, se actualizan los detalles del estudiantes (se crea otro porque el anterior se elimino)
                AbsentStudent absentStudent = existingAbsentStudents.stream()
                    .filter(absstudent -> absstudent.getStudent().getId().equals(dto.getId_student()))
                    .findFirst()
                    .orElse(new AbsentStudent());

                //Actualizando los ya existentes
                if(absentStudent.getId() != null){
                    if(!absentStudent.getCode().getId().equals(dto.getId_code())){
                        Code code = codeRepository.findById(dto.getId_code()).orElseThrow(() -> new NotFoundException("Code not found"));
                        absentStudent.setCode(code);
                    }
                    absentStudent.setComments(dto.getComments());
                    absentStudent.setDate(info.getUpdateDate());
                    return absentStudent;
                } else {
                    //si no existe, se crea uno nuevo
                    Student foundStudent = studentRepository.findById(dto.getId_student()).orElseThrow(() -> new NotFoundException("Student not found"));
                    absentStudent.setStudent(foundStudent);
                    Code foundCode = codeRepository.findById(dto.getId_code()).orElseThrow(() -> new NotFoundException("Code not found"));
                    absentStudent.setCode(foundCode);
                    absentStudent.setAbsenceRecord(absenceRecord);
                    absentStudent.setComments(dto.getComments());
                    absentStudent.setDate(info.getUpdateDate());
                    return absentStudent;
                }
            }).collect(Collectors.toList());
            //Guardando AbsentStudents
            savedAbsentStudents = absentStudentRepository.saveAll(updatedAbsentStudents);
        }

        AbsenceRecord savedAbsenceRecord = absenceRecordRepository.save(absenceRecord);

        AbsenceRecordWithStudentsDTO response = entityMapper.mapToAbsenceRecordWithAbsentStudentList(savedAbsenceRecord, savedAbsentStudents);
        return response;
    }

    @Override
    public long findByDate(LocalDate date) {
        return absenceRecordRepository.countAbsenceRecordsWithoutCoordinationValidation(date);
    }

    @Override
    public List<AbsenceRecordDTO> findByDateNoStudent(LocalDate date) {
        List<AbsenceRecord> absenceRecords = absenceRecordRepository.findByDate(date);
        if(absenceRecords.isEmpty() || absenceRecords == null){
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
        if(absenceRecords.isEmpty() || absenceRecords == null){
            return null;
        }
        List<AbsenceRecordDTO> response = absenceRecords.stream().map(entityMapper::map).collect(Collectors.toList());
        return response;
    }

    @Override
    public List<AbsenceRecordDTO> findByClassroom(UUID idClassroom) {
        Classroom classroom = classroomRepository.findById(idClassroom).orElseThrow(() -> new NotFoundException("Classroom not found"));
        List<AbsenceRecord> absenceRecords = absenceRecordRepository.findByClassroom(classroom);
        if(absenceRecords.isEmpty() || absenceRecords == null){
            return null;
        }

        List<AbsenceRecordDTO> response = absenceRecords.stream().map(entityMapper::map).collect(Collectors.toList());
        return response;
    }

    @Override
    public List<AbsenceRecordWithStudentsDTO> findByClassroomAndShift(UUID idClassroom, UUID shiftId) {
        Classroom classroom = classroomRepository.findById(idClassroom).orElseThrow(() -> new NotFoundException("Classroom not found"));
        
        
        List<AbsenceRecord> absenceRecord = absenceRecordRepository.findByClassroomAndShift(classroom.getId(), shiftId);
        List<AbsenceRecordWithStudentsDTO> response = entityMapper.mapToAbsenceRecordWithCustomClassroomDTOList(absenceRecord);

        if (absenceRecord == null || absenceRecord.isEmpty()) {
            return null;
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
    public AbsenceRecordDTO findByDateAndClassroom(LocalDate date, UUID idClassrooms) {
        Classroom classroom = classroomRepository.findById(idClassrooms).orElseThrow(() -> new NotFoundException("Classroom not found"));
        AbsenceRecord absenceRecord = absenceRecordRepository.findByDateAndClassroom(date, classroom);

        if (absenceRecord == null) {
            return null;
        }
        
        //mappeando a absence recorddto
        AbsenceRecordDTO response = entityMapper.map(absenceRecord);
        return response;

    }

    @Override
    public AbsenceRecordWithStudentsAttendanceDTO findByDateAndClassroomWithStudents(LocalDate date, UUID idClassrooms) {
        Classroom classroom = classroomRepository.findById(idClassrooms).orElseThrow(() -> new NotFoundException("Classroom not found"));
        AbsenceRecord absenceRecord = absenceRecordRepository.findByDateAndClassroom(date, classroom);

        if (absenceRecord == null) {
            return null;
        }

        // Get all students in the classroom
        List<Student> allStudents = studentXClassroomRepository.findStudentsByClassroomId(classroom.getId());

        // Initialize the list of absent students
        List<AbsentStudent> absentStudents = absenceRecord.getAbsentStudents();

        // Map absent students by their student IDs for quick access
        Map<UUID, AbsentStudent> absentStudentMap = absentStudents.stream()
            .collect(Collectors.toMap(
                absentStudent -> absentStudent.getStudent().getId(),
                absentStudent -> absentStudent
            ));

        // Create a list of StudentAttendanceDTO
        List<StudentAttendanceDTO> studentAttendanceList = allStudents.stream()
            .map(student -> {
                AbsentStudent absentStudent = absentStudentMap.get(student.getId());
                if (absentStudent != null) {
                    // Student is absent; include their absence details
                    return new StudentAttendanceDTO(
                        absentStudent.getId(),
                        absentStudent.getDate(),
                        absentStudent.getComments(),
                        student,
                        absentStudent.getCode()
                    );
                } else {
                    // Student is present; set date, comments, and code to null
                    return new StudentAttendanceDTO(
                        null,
                        null,
                        null,
                        student,
                        null
                    );
                }
            })
            .collect(Collectors.toList());

        // Build the response DTO
        AbsenceRecordWithStudentsAttendanceDTO response = new AbsenceRecordWithStudentsAttendanceDTO();
        response.setId(absenceRecord.getId());
        response.setDate(date);
        response.setMaleAttendance(absenceRecord.getMaleAttendance());
        response.setFemaleAttendance(absenceRecord.getFemaleAttendance());
        response.setTeacherValidation(absenceRecord.getTeacherValidation());
        response.setCoordinationValidation(absenceRecord.getCoordinationValidation());
        response.setClassroom(entityMapper.map(classroom));
        response.setAbsentStudents(studentAttendanceList);

        return response;
    }

    @Override
    public List<AbsenceRecord> findByClassroomAndShiftAndYear(UUID idClassroom, UUID idShift, int year) {
        Classroom classroom = classroomRepository.findById(idClassroom).orElseThrow(() -> new NotFoundException("Classroom not found"));
        return absenceRecordRepository.findByClassroomAndShiftAndYear(classroom, idShift, year);
    }

    @Override
    public List<AbsenceRecordDTO> findByDateAndShift(LocalDate date, UUID idShift) {
        List<AbsenceRecord> absenceRecords = absenceRecordRepository.findByDateAndShift(date, idShift);
        if (absenceRecords == null|| absenceRecords.isEmpty()) {
            return null;
        }
        List<AbsenceRecordDTO> response = absenceRecords.stream().map(entityMapper::map).collect(Collectors.toList());
        return response;
    }

    @Override
    public List<AbsenceRecordDTO> findByUserAndDate(UUID userId, LocalDate date) {
        //obtener al usuario desde el token
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<AbsenceRecord> absenceRecords = absenceRecordRepository.findByUserAndDate(user.getId(), date);
        if(absenceRecords.isEmpty() || absenceRecords == null){
            return null;
        }
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
        // List<Object[]> results = absentStudentRepository.findAllAbsentStudentByClassroomWithAbsenceType(classroomId, year);
        List<Object[]> results = absentStudentRepository.findAllAbsentStudentByClassroomWithAbsenceType(classroomId);

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
    public TopStudentsByShiftDTO getTopAbsentStudentsByMonth(LocalDate date){
        int month = date.getMonthValue();
        int year = date.getYear();

        // Assuming you have two shifts: "Morning" and "Afternoon"
        Shift morningShift = shiftRepository.findByName("Matutino");
        Shift afternoonShift = shiftRepository.findByName("Vespertino");

        List<Object[]> morningResults = absentStudentRepository.findTopStudentAbsencesByMonthAndShift(month, year, morningShift.getId());
        List<Object[]> afternoonResults = absentStudentRepository.findTopStudentAbsencesByMonthAndShift(month, year, afternoonShift.getId());

        TopStudentAbsenceDTO morningTopStudent = null;
        TopStudentAbsenceDTO afternoonTopStudent = null;

        if (!morningResults.isEmpty()) {
            Object[] result = morningResults.get(0);
            Student student = (Student) result[0];
            Classroom classroom = (Classroom) result[1];
            Long totalAbsences = (Long) result[2];
            morningTopStudent = new TopStudentsByShiftDTO.TopStudentAbsenceDTO(student, entityMapper.map(classroom), totalAbsences);
        }

        if (!afternoonResults.isEmpty()) {
            Object[] result = afternoonResults.get(0);
            Student student = (Student) result[0];
            Classroom classroom = (Classroom) result[1];
            Long totalAbsences = (Long) result[2];
            afternoonTopStudent = new TopStudentAbsenceDTO(student, entityMapper.map(classroom), totalAbsences);
        }

        return new TopStudentsByShiftDTO(morningTopStudent, afternoonTopStudent);
    }

    @Override
    public List<StudentAbsenceCountDTO> getTopAbsenceStudentsCountByTokenAndShift(UUID userId, UUID shift, String year) {
        List<Classroom> classrooms = classroomService.getClassroomsByUser(userId);
        List<UUID> classroomIds = classrooms.stream().map(Classroom::getId).collect(Collectors.toList());
        Pageable pageable = PageRequest.of(0, 2);
        
        List<Object[]> results = absentStudentRepository.findTopAbsentStudentsByClassroomsAndShiftAndYear(classroomIds, shift, year, pageable);
    
        return results.stream()
            .map(result -> {
                Long unjustifiedAbsences = (Long) result[2];
                Long justifiedAbsences = (Long) result[3];
                Long totalAbsences = unjustifiedAbsences + justifiedAbsences;
        
                return new StudentAbsenceCountDTO(
                    (Student) result[0], 
                    entityMapper.map((Classroom) result[1]),
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
