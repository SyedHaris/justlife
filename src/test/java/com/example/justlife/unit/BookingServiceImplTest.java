package com.example.justlife.unit;

import com.example.justlife.enums.BookingStatus;
import com.example.justlife.exception.BookingException;
import com.example.justlife.exception.CustomerNotFoundException;
import com.example.justlife.exception.HolidayException;
import com.example.justlife.model.BookedSlot;
import com.example.justlife.model.Booking;
import com.example.justlife.repository.BookedSlotRepository;
import com.example.justlife.repository.BookingRepository;
import com.example.justlife.repository.CleaningProfessionalRepository;
import com.example.justlife.repository.CustomerRepository;
import com.example.justlife.service.BookingServiceImpl;
import com.example.justlife.service.ScheduleConfigurationServiceImpl;
import com.example.justlife.util.Helper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.example.justlife.fixture.BookingFixture.*;
import static com.example.justlife.fixture.CleaningProfessionalFixture.cleaningProfessional;
import static com.example.justlife.fixture.CleaningProfessionalFixture.cleaningProfessional2;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    private BookingServiceImpl service;
    @Mock
    private ScheduleConfigurationServiceImpl scheduleConfigurationServiceImpl;
    @Mock
    private CleaningProfessionalRepository cleaningProfessionalRepository;
    @Mock
    private BookedSlotRepository bookedSlotRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CustomerRepository customerRepository;
    private final Helper helper = new Helper();
    @Captor
    private ArgumentCaptor<Booking> bookingCaptor;
    @Captor
    private ArgumentCaptor<List<BookedSlot>> bookedSlotsCaptor;

    @BeforeEach
    void init_mocks() {
        service = new BookingServiceImpl(
                scheduleConfigurationServiceImpl,
                cleaningProfessionalRepository,
                bookedSlotRepository,
                bookingRepository,
                customerRepository,
                helper
        );
    }

    @Nested
    class CreateBooking {
        @Test
        public void testBookingIsCreatedSuccessfully() {

            when(scheduleConfigurationServiceImpl.getScheduleConfiguration()).thenReturn(
                    configuration
            );

            when(customerRepository.findById(any())).thenReturn(
                    Optional.of(customer)
            );

            when(cleaningProfessionalRepository.findByIdIn(any())).thenReturn(
                    List.of(cleaningProfessional)
            );

            when(bookedSlotRepository.findBookedCleanerSlotsInDateTimeRange(any(), any(),
                    any(), any(), anyList(), any())).thenReturn(
                    Collections.emptyList()
            );

            when(bookingRepository.save(any())).thenReturn(
                    booking
            );

            var result = service.create(createBookingRequestDto, 1L);

            verify(bookingRepository, times(1)).save(bookingCaptor.capture());
            verify(bookedSlotRepository, times(1)).saveAll(bookedSlotsCaptor.capture());

            assertEquals(1L, result.bookingId());

            var createdBooking = bookingCaptor.getValue();
            assertEquals(date, createdBooking.getDate());
            assertEquals(BookingStatus.PENDING, createdBooking.getStatus());
            assertEquals(1, createdBooking.getCleaningProfessionals().size());
            assertEquals(LocalTime.of(8, 30), createdBooking.getStartTime());
            assertEquals(LocalTime.of(10, 30), createdBooking.getEndTime());

            var createdBookedSlot = bookedSlotsCaptor.getValue().get(0);
            assertEquals(date, createdBookedSlot.getDate());
            assertEquals(LocalTime.of(8, 30), createdBookedSlot.getStartTime());
            assertEquals(LocalTime.of(11, 0), createdBookedSlot.getEndTime());
            assertEquals("test2@example.com", createdBookedSlot.getCleaningProfessional().getEmail());
        }

        @Test
        public void testExceptionIsThrownWhenInputDateIsHoliday() {

            when(scheduleConfigurationServiceImpl.getScheduleConfiguration()).thenReturn(
                    configuration
            );

            when(scheduleConfigurationServiceImpl.isHoliday(any(), any())).thenCallRealMethod();

            when(customerRepository.findById(any())).thenReturn(
                    Optional.of(customer)
            );

            var exception = assertThrows(
                    HolidayException.class,
                    () -> service.create(createHolidayBookingRequestDto, 1L));

            assertEquals("Booking can't be created on a holiday", exception.getMessage());
        }

        @Test
        public void testExceptionIsThrownWhenCustomerIdIsInvalid() {

            when(scheduleConfigurationServiceImpl.getScheduleConfiguration()).thenReturn(
                    configuration
            );

            when(customerRepository.findById(any())).thenReturn(
                    Optional.empty()
            );

            var exception = assertThrows(
                    CustomerNotFoundException.class,
                    () -> service.create(createBookingRequestDto, 1L));

            assertEquals("Customer doesn't exist", exception.getMessage());
        }

        @Test
        public void testExceptionIsThrownWhenStartTimeIsOutOfRange() {

            when(scheduleConfigurationServiceImpl.getScheduleConfiguration()).thenReturn(
                    configuration
            );

            when(customerRepository.findById(any())).thenReturn(
                    Optional.of(customer)
            );

            var exception = assertThrows(
                    BookingException.class,
                    () -> service.create(createStartOutOfRangeBookingRequestDto, 1L));

            assertEquals("Provided time slot is invalid", exception.getMessage());
        }

        @Test
        public void testExceptionIsThrownWhenEndTimeIsOutOfRange() {

            when(scheduleConfigurationServiceImpl.getScheduleConfiguration()).thenReturn(
                    configuration
            );

            when(customerRepository.findById(any())).thenReturn(
                    Optional.of(customer)
            );

            var exception = assertThrows(
                    BookingException.class,
                    () -> service.create(createEndOutOfRangeBookingRequestDto, 1L));

            assertEquals("Provided time slot is invalid", exception.getMessage());
        }

        @Test
        public void testExceptionIsThrownWhenCleaningProfessionalsBelongToDifferentVehicle() {

            when(scheduleConfigurationServiceImpl.getScheduleConfiguration()).thenReturn(
                    configuration
            );

            when(customerRepository.findById(any())).thenReturn(
                    Optional.of(customer)
            );

            when(cleaningProfessionalRepository.findByIdIn(any())).thenReturn(
                    List.of(
                            cleaningProfessional,
                            cleaningProfessional2
                    )
            );

            var exception = assertThrows(
                    BookingException.class,
                    () -> service.create(createEndOutOfRangeBookingRequestDto, 1L));

            assertEquals("Provided cleaning professionals are not valid for this booking", exception.getMessage());
        }

        @Test
        public void testExceptionIsThrownWhenSlotIsNotAvailable() {

            when(scheduleConfigurationServiceImpl.getScheduleConfiguration()).thenReturn(
                    configuration
            );

            when(customerRepository.findById(any())).thenReturn(
                    Optional.of(customer)
            );

            when(bookedSlotRepository.findBookedCleanerSlotsInDateTimeRange(any(), any(),
                    any(), any(), anyList(), any())).thenReturn(
                    List.of(bookedSlot)
            );

            var exception = assertThrows(
                    BookingException.class,
                    () -> service.create(createBookingRequestDto, 1L));

            assertEquals("Slot not available", exception.getMessage());
        }
    }

    @Nested
    class UpdateBooking {
        @Test
        public void testBookingIsUpdatedSuccessfully() {

            when(scheduleConfigurationServiceImpl.getScheduleConfiguration()).thenReturn(
                    configuration
            );

            when(bookingRepository.findFirstByIdAndCustomerId(any(), any())).thenReturn(
                    Optional.of(booking)
            );

            when(bookedSlotRepository.findBookedCleanerSlotsInDateTimeRange(any(), any(),
                    any(), any(), anyList(), any())).thenReturn(
                    Collections.emptyList()
            );

            when(bookedSlotRepository.findByBookingId(any())).thenReturn(
                    List.of(bookedSlot)
            );


            var result = service.update(updateBookingRequestDto, 1L, 1L);

            verify(bookingRepository, times(1)).save(bookingCaptor.capture());
            verify(bookedSlotRepository, times(1)).saveAll(bookedSlotsCaptor.capture());

            assertEquals(LocalDate.of(2024, 7, 10), result.date());
            assertEquals("09:30", result.startTime());
            assertEquals("11:30", result.endTime());

            var createdBooking = bookingCaptor.getValue();
            assertEquals(date, createdBooking.getDate());
            assertEquals(BookingStatus.PENDING, createdBooking.getStatus());
            assertEquals(1, createdBooking.getCleaningProfessionals().size());
            assertEquals(LocalTime.of(9, 30), createdBooking.getStartTime());
            assertEquals(LocalTime.of(11, 30), createdBooking.getEndTime());

            var createdBookedSlot = bookedSlotsCaptor.getValue().get(0);
            assertEquals(date, createdBookedSlot.getDate());
            assertEquals(LocalTime.of(9, 30), createdBookedSlot.getStartTime());
            assertEquals(LocalTime.of(12, 0), createdBookedSlot.getEndTime());
            assertEquals("test2@example.com", createdBookedSlot.getCleaningProfessional().getEmail());
        }

        @Test
        public void testExceptionIsThrownWhenInputDateIsHoliday() {

            when(scheduleConfigurationServiceImpl.getScheduleConfiguration()).thenReturn(
                    configuration
            );

            when(bookingRepository.findFirstByIdAndCustomerId(any(), any())).thenReturn(
                    Optional.of(booking)
            );

            when(scheduleConfigurationServiceImpl.isHoliday(any(), any())).thenCallRealMethod();

            var exception = assertThrows(
                    HolidayException.class,
                    () -> service.update(updateHolidayBookingRequestDto, 1L, 1L));

            assertEquals("Booking can't be created on a holiday", exception.getMessage());
        }

        @Test
        public void testExceptionIsThrownWhenBookingAndCustomerIsInvalid() {

            when(bookingRepository.findFirstByIdAndCustomerId(any(), any())).thenReturn(
                    Optional.empty()
            );

            var exception = assertThrows(
                    BookingException.class,
                    () -> service.update(updateBookingRequestDto, 1L, 1L));

            assertEquals("Booking doesn't exist", exception.getMessage());
        }

        @Test
        public void testExceptionIsThrownWhenStartTimeIsOutOfRange() {

            when(scheduleConfigurationServiceImpl.getScheduleConfiguration()).thenReturn(
                    configuration
            );

            when(bookingRepository.findFirstByIdAndCustomerId(any(), any())).thenReturn(
                    Optional.of(booking)
            );

            var exception = assertThrows(
                    BookingException.class,
                    () -> service.update(updateStartOutOfRangeBookingRequestDto, 1L, 1L));

            assertEquals("Provided time slot is invalid", exception.getMessage());
        }

        @Test
        public void testExceptionIsThrownWhenEndTimeIsOutOfRange() {

            when(scheduleConfigurationServiceImpl.getScheduleConfiguration()).thenReturn(
                    configuration
            );

            when(bookingRepository.findFirstByIdAndCustomerId(any(), any())).thenReturn(
                    Optional.of(booking)
            );

            var exception = assertThrows(
                    BookingException.class,
                    () -> service.update(updateEndOutOfRangeBookingRequestDto, 1L, 1L));

            assertEquals("Provided time slot is invalid", exception.getMessage());
        }

        @Test
        public void testExceptionIsThrownWhenSlotIsNotAvailable() {

            when(scheduleConfigurationServiceImpl.getScheduleConfiguration()).thenReturn(
                    configuration
            );

            when(bookingRepository.findFirstByIdAndCustomerId(any(), any())).thenReturn(
                    Optional.of(booking)
            );

            when(bookedSlotRepository.findBookedCleanerSlotsInDateTimeRange(any(), any(),
                    any(), any(), anyList(), any())).thenReturn(
                    List.of(bookedSlot)
            );

            var exception = assertThrows(
                    BookingException.class,
                    () -> service.update(updateBookingRequestDto, 1L, 1L));

            assertEquals("Slot not available", exception.getMessage());
        }
    }
}