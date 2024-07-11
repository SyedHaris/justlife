package com.example.justlife.unit;

import com.example.justlife.repository.ScheduleConfigurationRepository;
import com.example.justlife.service.ScheduleConfigurationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.util.Collections;

import static com.example.justlife.fixture.BookingFixture.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScheduleConfigurationServiceImplTest {
    private ScheduleConfigurationServiceImpl service;
    @Mock
    private ScheduleConfigurationRepository scheduleConfigurationRepository;

    @BeforeEach
    void init_mocks() {
        service = new ScheduleConfigurationServiceImpl(
                scheduleConfigurationRepository
        );
    }

    @Test
    void getScheduleConfiguration() {
        when(scheduleConfigurationRepository.findAll()).thenReturn(
                Collections.singletonList(configuration)
        );

        var result = service.getScheduleConfiguration();

        assertEquals(DayOfWeek.FRIDAY, result.getHoliday());
        assertEquals(configuration.getStartTime(), result.getStartTime());
        assertEquals(configuration.getEndTime(), result.getEndTime());
        assertEquals(configuration.getBreakDurationMinutes(), result.getBreakDurationMinutes());
    }
}