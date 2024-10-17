package com.masferrer.services.implementations;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.masferrer.models.dtos.ShowWeekdayDTO;
import com.masferrer.models.entities.Weekday;
import com.masferrer.repository.WeekDayRepository;
import com.masferrer.services.WeekdayService;

@Service
public class WeekdayServiceImpl implements WeekdayService {
    @Autowired 
    WeekDayRepository weekDayRepository;

    @Override
    public List<ShowWeekdayDTO> findAllWeekdays() {
        List<Weekday> weekdays = weekDayRepository.findAll();
        List<ShowWeekdayDTO> weekdayDTO = weekdays.stream()
            .map(weekday -> new ShowWeekdayDTO(weekday.getId(),weekday.getDay()))
            .collect(Collectors.toList());
        return weekdayDTO;
    }

}
