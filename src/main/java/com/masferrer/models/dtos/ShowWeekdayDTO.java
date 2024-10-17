package com.masferrer.models.dtos;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ShowWeekdayDTO {
    private UUID id;
    private String day;
}
