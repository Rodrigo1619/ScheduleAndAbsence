package com.masferrer.models.entities;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "class_period", schema = "public")
public class ClassPeriod {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "classPeriod")
    @JsonIgnore
    private List<ClassroomConfiguration> classroomConfigurations;
}
