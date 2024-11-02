package com.masferrer.models.entities;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

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
@Table(name = "absence_record", schema = "public")
public class AbsenceRecord {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "male_attendance")
    private Integer maleAttendance;

    @Column(name = "female_attendance")
    private Integer femaleAttendance;

    @Column(name = "teacher_validation")
    private Boolean teacherValidation;

    @Column(name = "coordination_validation")
    private Boolean coordinationValidation;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_classroom")
    private Classroom classroom;

    @OneToMany(mappedBy = "absenceRecord", fetch = FetchType.LAZY)
    @JsonManagedReference
    @JsonIgnore
    private List<AbsentStudent> absentStudents;

    public AbsenceRecord( LocalDate date, Classroom classroom, Integer maleAttendance, Integer femaleAttendance) {
        this.date = date;
        this.classroom = classroom;
        this.maleAttendance = maleAttendance;
        this.femaleAttendance = femaleAttendance;
    }
}
