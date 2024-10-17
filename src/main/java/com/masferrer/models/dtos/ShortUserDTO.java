package com.masferrer.models.dtos;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShortUserDTO {
    private UUID id;
    private String name;
    private String email;
    private Boolean active;
    private String verifiedEmail;
}
