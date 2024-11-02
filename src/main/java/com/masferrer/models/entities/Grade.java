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
@Table(name = "grade", schema = "public")
public class Grade {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "id_goverment")
    private String idGoverment;

    @Column(name = "section")
    private String section;

    //Agregar a base de datos
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_shift")
    private Shift shift;

    @OneToMany(mappedBy = "grade", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Classroom> classrooms;

    public Grade(String name, String idGoverment, String section, Shift shift) {
        this.name = name;
        this.idGoverment = idGoverment;
        this.section = section;
        this.shift = shift;
    }
}
