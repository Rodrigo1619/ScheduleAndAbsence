package com.masferrer.models.entities;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "classroom", schema = "public")
public class Classroom {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "year")
    private String year;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_grade")
    private Grade grade;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_teacher")
    private User user;

    @OneToMany(mappedBy = "classroom", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Student_X_Classroom> listStudentXClassroom;

    @OneToMany(mappedBy = "classroom", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<ClassroomConfiguration> classroomConfigurations;
    
    @OneToMany(mappedBy = "classroom", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<AbsenceRecord> absenceRecords;
    
    public Classroom(String year, Grade grade, User teacher) {
        this.year = year;
        this.grade = grade;
        // this.shift = shift;
        this.user = teacher;
    }
}
