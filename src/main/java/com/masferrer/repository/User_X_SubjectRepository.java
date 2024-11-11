package com.masferrer.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.masferrer.models.entities.Subject;
import com.masferrer.models.entities.User;
import com.masferrer.models.entities.User_X_Subject;

public interface User_X_SubjectRepository  extends JpaRepository<User_X_Subject, UUID>{
    
    @Query("SELECT u.user FROM User_X_Subject u WHERE u.subject.id = :subjectId ORDER BY u.user.name ASC")
    List<User> findUsersBySubjectId(@Param("subjectId") UUID subjectId);

    @Query("SELECT u.subject FROM User_X_Subject u WHERE u.user.id = :userId ORDER BY u.subject.name ASC")
    List<Subject> findSubjectsByUserId(@Param("userId") UUID userId);

    //esto es para poder ver si ya existe el usuario asignado a una materia
    //se cuenta a los usuarios, ya que si es mayor a 0, quiere decir que ya existe en esa materia
    @Query("SELECT COUNT(u) > 0 FROM User_X_Subject u WHERE u.user.id = :userId AND u.subject.id = :subjectId")
    boolean existsByUserIdAndSubjectId(@Param("userId") UUID userId, @Param("subjectId") UUID subjectId);

    //con esto espero que pueda servir para el schedule
    User_X_Subject findByUserAndSubject(User user, Subject subject);
}
