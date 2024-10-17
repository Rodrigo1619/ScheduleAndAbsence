package com.masferrer.models.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ForgotPasswordDTO {
    @NotBlank(message = "email must not be empty")
    private String email;
    @NotBlank(message = "New password must not be empty")
    private String newPassword;

}
