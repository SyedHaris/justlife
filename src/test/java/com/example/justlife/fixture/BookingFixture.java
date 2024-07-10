package com.example.justlife.fixture;

import com.example.justlife.dto.request.CreateBookingRequestDto;
import com.example.justlife.dto.request.UpdateBookingRequestDto;
import com.example.justlife.dto.response.CreateBookingResponseDto;
import com.example.justlife.dto.response.UpdateBookingResponseDto;
import com.example.justlife.enums.BookingStatus;
import com.example.justlife.model.BookedSlot;
import com.example.justlife.model.Booking;
import com.example.justlife.model.CleaningProfessional;
import com.example.justlife.model.Customer;
import com.example.justlife.model.ScheduleConfiguration;
import com.example.justlife.model.Vehicle;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

public class BookingFixture {
    public static final LocalDate date = LocalDate.of(2024, 7, 10);
    public static final LocalDate holidayDate = LocalDate.of(2024, 7, 12);
    public static final CreateBookingRequestDto createBookingRequestDto = new CreateBookingRequestDto(date, "08:30", (short) 2, List.of(1L));
    public static final CreateBookingRequestDto createHolidayBookingRequestDto = new CreateBookingRequestDto(holidayDate, "8:30", (short) 2, List.of(1L));
    public static final CreateBookingRequestDto createStartOutOfRangeBookingRequestDto = new CreateBookingRequestDto(holidayDate, "7:00", (short) 2, List.of(1L));
    public static final CreateBookingRequestDto createEndOutOfRangeBookingRequestDto = new CreateBookingRequestDto(holidayDate, "19:00", (short) 4, List.of(1L));
    public static final CreateBookingRequestDto createInvalidParamsBookingRequestDto = new CreateBookingRequestDto(null, "19:00", (short) 4, List.of(1L));
    public static final ScheduleConfiguration configuration = new ScheduleConfiguration(1L, LocalTime.of(8, 0), LocalTime.of(22, 0), 30, DayOfWeek.FRIDAY);
    public static final Customer customer = new Customer(1L, "test-customer", "test@example.com", "+921234567891", "test", "example.com");
    public static final Vehicle vehicle = new Vehicle(1L, "ABC-123", "test-driver", "test-make", "test-model", "2024");
    public static final Vehicle vehicle2 = new Vehicle(2L, "ABC-145", "test-driver-2", "test-make", "test-model", "2024");
    public static final CleaningProfessional cleaningProfessional = new CleaningProfessional(1L, "test-cleaning-professional-1", "test2@example.com", "example.com", 4.5, vehicle, null);
    public static final CleaningProfessional cleaningProfessional2 = new CleaningProfessional(1L, "test-cleaning-professional-2", "test3@example.com", "example.com", 4.0, vehicle2, null);
    public static final Booking booking = new Booking(1L, date, LocalTime.of(8, 30), LocalTime.of(10, 30), BookingStatus.PENDING, customer, Set.of(cleaningProfessional));
    public static final BookedSlot bookedSlots = new BookedSlot(1L, date, LocalTime.of(9, 0), LocalTime.of(11, 0), booking, cleaningProfessional);
    public static final CreateBookingResponseDto createBookingResponseDto = new CreateBookingResponseDto(10L);
    public static final UpdateBookingRequestDto updateBookingRequestDto = new UpdateBookingRequestDto(date, "09:30", (short) 2);
    public static final UpdateBookingRequestDto updateHolidayBookingRequestDto = new UpdateBookingRequestDto(holidayDate, "09:30", (short) 2);
    public static final UpdateBookingRequestDto updateStartOutOfRangeBookingRequestDto = new UpdateBookingRequestDto(date, "07:30", (short) 2);
    public static final UpdateBookingRequestDto updateEndOutOfRangeBookingRequestDto = new UpdateBookingRequestDto(date, "21:30", (short) 2);
    public static final UpdateBookingResponseDto updateBookingResponseDto = new UpdateBookingResponseDto(1L, date, "9:30", "11:30");
    public static final UpdateBookingRequestDto updateInvalidParamsBookingRequestDto = new UpdateBookingRequestDto(date, "07:30", (short) 8);
}
