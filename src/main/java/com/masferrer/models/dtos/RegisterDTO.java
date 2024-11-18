package com.masferrer.models.dtos;

import java.util.UUID;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
public class RegisterDTO {
    @NotBlank(message = "Name must not be empty")
    private String name;

    @NotBlank(message = "Email must not be empty")
    @Email
    private String email;

    @NotBlank(message = "Verified Email must not be empty")
    @Email
    private String verified_email;


    @NotBlank(message = "Password must not be empty")
    @Size(min = 8)
	@Pattern(regexp = "^(?=.*[0-9]).*$", message = "password must have at leat one number")
	@Pattern(regexp = "^(?=.*[a-z]).*$", message = "password must have at leat one lowercase letter")
	@Pattern(regexp = "^(?=.*[A-Z]).*$", message = "password must have at leat one uppercase letter")
    @Pattern(regexp = "^(?=.*[@#$%^&+=!{}.,<>\\-+*;:'/\\?¡¿_]).*$", message = "Password must have at least one special character")
	private String password;

    @NotNull(message = "Role id must not be empty")
    private UUID id_role;
}
