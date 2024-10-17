package com.masferrer.models.dtos;

import java.util.UUID;

import com.masferrer.models.entities.Subject;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserXSubjectDTO {

    private UUID id;
    private ShortUserDTO teacher;
    private Subject subject;
}
