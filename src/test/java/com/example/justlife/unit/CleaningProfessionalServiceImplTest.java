package com.example.justlife.unit;

import com.example.justlife.repository.BookedSlotRepository;
import com.example.justlife.repository.CleaningProfessionalRepository;
import com.example.justlife.service.BookingService;
import com.example.justlife.service.CleaningProfessionalServiceImpl;
import com.example.justlife.service.ScheduleConfigurationServiceImpl;
import com.example.justlife.util.Helper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static com.example.justlife.fixture.BookingFixture.*;
import static com.example.justlife.fixture.BookingFixture.configuration;
import static com.example.justlife.fixture.CleaningProfessionalFixture.cleaningProfessional;
import static com.example.justlife.fixture.CleaningProfessionalFixture.cleaningProfessional2;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CleaningProfessionalServiceImplTest {
    private CleaningProfessionalServiceImpl service;
    @Mock
    private ScheduleConfigurationServiceImpl scheduleConfigurationServiceImpl;
    @Mock
    private CleaningProfessionalRepository cleaningProfessionalRepository;
    @Mock
    private BookingService bookingService;

    private final Helper helper = new Helper();
    @Mock
    private BookedSlotRepository bookedSlotRepository;

    @BeforeEach
    void init_mocks() {
        service = new CleaningProfessionalServiceImpl(
                scheduleConfigurationServiceImpl,
                cleaningProfessionalRepository,
                bookingService,
                helper,
                bookedSlotRepository
        );
    }

    @Nested
    class GetAvailability {
        @Test
        public void shouldReturnCleaningProfessionalsWhenDateTimeAndDurationIsProvided() {

            when(bookingService.getBookedSlots(any(), any(),
                    any(), any(), any())).thenReturn(
                    List.of(bookedSlot)
            );

            when(cleaningProfessionalRepository.findByIdNotIn(anyList(), any())).thenReturn(
                    List.of(cleaningProfessional2)
            );


            var result = service.getAvailability(date, "08:30", Optional.of((short)2), 0, 10);

            verify(cleaningProfessionalRepository, times(1)).findByIdNotIn(eq(List.of(cleaningProfessional.getId())), any());

            assertEquals(1, result.size());
            assertEquals(cleaningProfessional2.getId(), result.get(0).id());
            assertEquals(cleaningProfessional2.getName(), result.get(0).name());
            assertEquals(cleaningProfessional2.getEmail(), result.get(0).email());
            assertNull(result.get(0).availableSlots());
        }

        @Test
        public void shouldReturnEmptyListWhenHolidayDateIsProvided() {

            when(scheduleConfigurationServiceImpl.getScheduleConfiguration()).thenReturn(
                    configuration
            );

            when(scheduleConfigurationServiceImpl.isHoliday(any(), any())).thenCallRealMethod();

            var result = service.getAvailability(holidayDate, "08:30", Optional.of((short)1), 0, 10);

            assertEquals(0, result.size());
        }

        @Test
        public void shouldReturnCleaningProfessionalsWithAvailableSlotsWhenOnlyDateIsProvided() {

            when(cleaningProfessionalRepository.findAll(any(Pageable.class))).thenReturn(
                    new PageImpl<>(List.of(cleaningProfessional, cleaningProfessional2), PageRequest.of(0, 10), 0)
            );

            when(bookedSlotRepository.findByCleaningProfessionalIdAndDate(any(), any())).thenReturn(
                    List.of(bookedSlot, bookedSlot2)
            );

            when(scheduleConfigurationServiceImpl.getScheduleConfiguration()).thenReturn(
                    configuration
            );

            var result = service.getAvailability(date, null, Optional.empty(), 0, 10);

            assertEquals(2, result.size());
            assertNotNull(result.get(0).availableSlots());
            assertNotNull(result.get(1).availableSlots());
            assertEquals("08:00-08:30,11:30-15:30,20:30-22:00", String.join(",", result.get(0).availableSlots()));
            assertEquals("08:00-08:30,11:30-15:30,20:30-22:00", String.join(",", result.get(1).availableSlots()));
        }
    }
}