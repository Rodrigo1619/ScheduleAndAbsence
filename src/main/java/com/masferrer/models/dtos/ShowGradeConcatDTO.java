package com.masferrer.models.dtos;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ShowGradeConcatDTO {
    UUID id;
    String name;
    String idGoverment;
    String section;
}
