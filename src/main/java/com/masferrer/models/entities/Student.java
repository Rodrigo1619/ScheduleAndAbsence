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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "student", schema = "public")
public class Student {
    
        @Id
        @Column(name = "id")
        @GeneratedValue(strategy = GenerationType.AUTO)
        private UUID id;
    
        @Column(name = "nie")
        private String nie;
    
        @Column(name = "name")
        private String name;

        @Column(name = "active", insertable = false)
        private Boolean active;

        @OneToMany(mappedBy = "student", fetch = FetchType.LAZY)
        @JsonIgnore
        private List<Student_X_Classroom> ListStudentXClassroom;

        @OneToMany(mappedBy = "student", fetch = FetchType.LAZY)
        @JsonIgnore
        private List<AbsentStudent> absentStudents;

        public Student(String nie, String name) {
            this.nie = nie;
            this.name = name;
        }
}
