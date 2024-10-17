package com.masferrer.models.dtos;

import java.util.UUID;

import com.masferrer.models.entities.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private UUID id;
    private String name;
    private String email;
    private Role role;
    private Boolean active;
    private String verifiedEmail;

}
