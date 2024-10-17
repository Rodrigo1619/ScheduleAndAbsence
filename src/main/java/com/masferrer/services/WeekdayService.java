package com.masferrer.services;

import java.util.List;

import com.masferrer.models.dtos.ShowWeekdayDTO;

public interface WeekdayService {
    List<ShowWeekdayDTO> findAllWeekdays();
}
