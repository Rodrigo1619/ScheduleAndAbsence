package com.masferrer.models.entities;

import java.util.UUID;

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
@Table(name = "schedule", schema = "public")
public class Schedule {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_user_x_subject")
    private User_X_Subject user_x_subject;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_classroom_configuration")
    private ClassroomConfiguration classroomConfiguration;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_weekday")
    private Weekday weekday;

    public Schedule(User_X_Subject user_x_subject, ClassroomConfiguration classConfiguration, Weekday weekday) {
        this.user_x_subject = user_x_subject;
        this.classroomConfiguration = classConfiguration;
        this.weekday = weekday;
        
    }
}
