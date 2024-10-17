package com.masferrer.models.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SaveSubjectDTO {

    @NotBlank(message = "Name is required")
    private String name;
}
