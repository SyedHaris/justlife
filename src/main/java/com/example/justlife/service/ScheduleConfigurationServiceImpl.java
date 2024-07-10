package com.example.justlife.service;

import com.example.justlife.exception.BookingException;
import com.example.justlife.model.ScheduleConfiguration;
import com.example.justlife.repository.ScheduleConfigurationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ScheduleConfigurationServiceImpl implements ScheduleConfigurationService {
    private final ScheduleConfigurationRepository scheduleConfigurationRepository;

    public ScheduleConfigurationServiceImpl(ScheduleConfigurationRepository scheduleConfigurationRepository) {
        this.scheduleConfigurationRepository = scheduleConfigurationRepository;
    }

    @Override
    public ScheduleConfiguration getScheduleConfiguration() {
        return scheduleConfigurationRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow(() -> new BookingException("Booking can't be created"));
    }

    @Override
    public boolean isHoliday(LocalDate date, ScheduleConfiguration scheduleConfiguration) {
        return scheduleConfiguration.getHoliday().equals(date.getDayOfWeek());
    }

}
