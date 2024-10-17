package com.masferrer.models.dtos;


import com.masferrer.models.entities.Role;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class WhoAmIDTO {
    private String name;
    private String email;
    private Role role;
    private String verifiedEmail;

}
