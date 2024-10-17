package com.masferrer.models.entities;

import java.time.LocalDate;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "absent_student", schema = "public")
public class AbsentStudent {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "comments")
    private String comments;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_student")
    private Student student;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_code")
    private Code code;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_absence_record")
    @JsonBackReference
    private AbsenceRecord absenceRecord;

    public AbsentStudent(LocalDate date, Student student, Code code, AbsenceRecord absenceRecord, String comments) {
        this.date = date;
        this.student = student;
        this.code = code;
        this.absenceRecord = absenceRecord;
        this.comments = comments;
    }
}
