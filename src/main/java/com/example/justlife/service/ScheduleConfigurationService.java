package com.example.justlife.service;

import com.example.justlife.model.ScheduleConfiguration;

import java.time.LocalDate;

public interface ScheduleConfigurationService {
    ScheduleConfiguration getScheduleConfiguration();

    boolean isHoliday(LocalDate date, ScheduleConfiguration scheduleConfiguration);

}
