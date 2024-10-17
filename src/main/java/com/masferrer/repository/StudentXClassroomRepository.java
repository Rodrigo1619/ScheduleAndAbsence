package com.masferrer.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.masferrer.models.entities.Classroom;
import com.masferrer.models.entities.Student;
import com.masferrer.models.entities.Student_X_Classroom;

public interface StudentXClassroomRepository extends JpaRepository<Student_X_Classroom, UUID>{

    @Query("SELECT s.student FROM Student_X_Classroom s WHERE s.classroom.id = :classroomId")
    List<Student> findStudentsByClassroomId(@Param("classroomId") UUID classroomId);

    @Query("SELECT sc FROM Student_X_Classroom sc WHERE sc.classroom.id = :classroomId")
    List<Student_X_Classroom> findEnrollmentsByClassroomId(@Param("classroomId") UUID classroomId);

    Student_X_Classroom findByStudentAndClassroom(Student student, Classroom classroom);

    @Query("SELECT sc FROM Student_X_Classroom sc JOIN sc.classroom c WHERE sc.student.id = :studentId AND c.year = :year")
    Student_X_Classroom findByStudentAndYear(@Param("studentId") UUID studentId, @Param("year") String year);

    @Query("SELECT sc.classroom FROM Student_X_Classroom sc JOIN sc.classroom c WHERE sc.student.nie = :nie AND c.year = :year")
    Classroom findByNieAndYear(@Param("nie") String nie, @Param("year") String year);

    @Query("SELECT s FROM Student s LEFT JOIN Student_X_Classroom sc ON s.id = sc.student.id WHERE sc.student.id IS NULL")
    List<Student> findStudentsNotInAnyClassroom();

    // @Query("SELECT s FROM Student s WHERE s.id NOT IN (SELECT sc.student.id FROM Student_X_Classroom sc)")
    // List<Student> findStudentsNotInClassroom();
}
