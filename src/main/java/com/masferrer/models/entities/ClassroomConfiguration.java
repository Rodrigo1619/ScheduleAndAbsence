package com.masferrer.models.entities;

import java.time.LocalTime;
import java.util.UUID;
import java.util.List;

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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "classroom_configuration", schema = "public")
public class ClassroomConfiguration {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "hour_start")
    private LocalTime hourStart;

    @Column(name = "hour_end")
    private LocalTime hourEnd;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_class_period")
    private ClassPeriod classPeriod;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_classroom")
    private Classroom classroom;

    @OneToMany(mappedBy = "classroomConfiguration", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Schedule> schedules;

    public ClassroomConfiguration(LocalTime hourStart, LocalTime hourEnd, ClassPeriod classPeriod, Classroom classroom) {
        this.hourStart = hourStart;
        this.hourEnd = hourEnd;
        this.classPeriod = classPeriod;
        this.classroom = classroom;
    }
}
