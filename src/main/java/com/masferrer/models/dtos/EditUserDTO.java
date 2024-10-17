package com.masferrer.models.dtos;

import java.util.UUID;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EditUserDTO {
    private String name;

    @Email
    private String email;

	private String password;

    private String verifiedEmail;

    private UUID id_role;
}
