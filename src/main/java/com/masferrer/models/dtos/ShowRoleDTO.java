package com.masferrer.models.dtos;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ShowRoleDTO {
    public UUID id;
    public String name;


}
