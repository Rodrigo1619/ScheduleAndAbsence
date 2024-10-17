package com.masferrer.models.dtos;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AssignSubjectToTeacherDTO {
    private UUID id_subject;
    private UUID id_user;
}
