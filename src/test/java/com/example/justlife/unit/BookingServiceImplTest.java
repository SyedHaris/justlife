package com.example.justlife.unit;

import com.example.justlife.enums.BookingStatus;
import com.example.justlife.exception.BookingException;
import com.example.justlife.exception.CustomerNotFoundException;
import com.example.justlife.exception.HolidayException;
import com.example.justlife.fixture.BookingFixture;
import com.example.justlife.model.BookedSlot;
import com.example.justlife.model.Booking;
import com.example.justlife.repository.BookedSlotRepository;
import com.example.justlife.repository.BookingRepository;
import com.example.justlife.repository.CleaningProfessionalRepository;
import com.example.justlife.repository.CustomerRepository;
import com.example.justlife.service.BookingServiceImpl;
import com.example.justlife.service.ScheduleConfigurationServiceImpl;
import com.example.justlife.util.Helper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {
    private BookingServiceImpl bookingServiceImpl;
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
        bookingServiceImpl = new BookingServiceImpl(
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
                    BookingFixture.configuration
            );

            when(customerRepository.findById(any())).thenReturn(
                    Optional.of(BookingFixture.customer)
            );

            when(cleaningProfessionalRepository.findByIdIn(any())).thenReturn(
                    List.of(BookingFixture.cleaningProfessional)
            );

            when(bookedSlotRepository.findBookedCleanerSlotsInDateTimeRange(any(), any(),
                    any(), any(), anyList(), any())).thenReturn(
                    Collections.emptyList()
            );

            when(bookingRepository.save(any())).thenReturn(
                    BookingFixture.booking
            );

            var result = bookingServiceImpl.create(BookingFixture.createBookingRequestDto, 1L);

            verify(bookingRepository, times(1)).save(bookingCaptor.capture());
            verify(bookedSlotRepository, times(1)).saveAll(bookedSlotsCaptor.capture());

            Assertions.assertEquals(1L, result.bookingId());

            var createdBooking = bookingCaptor.getValue();
            Assertions.assertEquals(BookingFixture.date, createdBooking.getDate());
            Assertions.assertEquals(BookingStatus.PENDING, createdBooking.getStatus());
            Assertions.assertEquals(1, createdBooking.getCleaningProfessionals().size());
            Assertions.assertEquals(LocalTime.of(8, 30), createdBooking.getStartTime());
            Assertions.assertEquals(LocalTime.of(10, 30), createdBooking.getEndTime());

            var createdBookedSlot = bookedSlotsCaptor.getValue().get(0);
            Assertions.assertEquals(BookingFixture.date, createdBookedSlot.getDate());
            Assertions.assertEquals(LocalTime.of(8, 30), createdBookedSlot.getStartTime());
            Assertions.assertEquals(LocalTime.of(11, 0), createdBookedSlot.getEndTime());
            Assertions.assertEquals("test2@example.com", createdBookedSlot.getCleaningProfessional().getEmail());
        }

        @Test
        public void testExceptionIsThrownWhenInputDateIsHoliday() {

            when(scheduleConfigurationServiceImpl.getScheduleConfiguration()).thenReturn(
                    BookingFixture.configuration
            );

            when(scheduleConfigurationServiceImpl.isHoliday(any(), any())).thenCallRealMethod();

            when(customerRepository.findById(any())).thenReturn(
                    Optional.of(BookingFixture.customer)
            );

            var exception = Assertions.assertThrows(
                    HolidayException.class,
                    () -> bookingServiceImpl.create(BookingFixture.createHolidayBookingRequestDto, 1L));

            Assertions.assertEquals("Booking can't be created on a holiday", exception.getMessage());
        }

        @Test
        public void testExceptionIsThrownWhenCustomerIdIsInvalid() {

            when(scheduleConfigurationServiceImpl.getScheduleConfiguration()).thenReturn(
                    BookingFixture.configuration
            );

            when(customerRepository.findById(any())).thenReturn(
                    Optional.empty()
            );

            var exception = Assertions.assertThrows(
                    CustomerNotFoundException.class,
                    () -> bookingServiceImpl.create(BookingFixture.createBookingRequestDto, 1L));

            Assertions.assertEquals("Customer doesn't exist", exception.getMessage());
        }

        @Test
        public void testExceptionIsThrownWhenStartTimeIsOutOfRange() {

            when(scheduleConfigurationServiceImpl.getScheduleConfiguration()).thenReturn(
                    BookingFixture.configuration
            );

            when(customerRepository.findById(any())).thenReturn(
                    Optional.of(BookingFixture.customer)
            );

            var exception = Assertions.assertThrows(
                    BookingException.class,
                    () -> bookingServiceImpl.create(BookingFixture.createStartOutOfRangeBookingRequestDto, 1L));

            Assertions.assertEquals("Provided provided time slot is invalid", exception.getMessage());
        }

        @Test
        public void testExceptionIsThrownWhenEndTimeIsOutOfRange() {

            when(scheduleConfigurationServiceImpl.getScheduleConfiguration()).thenReturn(
                    BookingFixture.configuration
            );

            when(customerRepository.findById(any())).thenReturn(
                    Optional.of(BookingFixture.customer)
            );

            var exception = Assertions.assertThrows(
                    BookingException.class,
                    () -> bookingServiceImpl.create(BookingFixture.createEndOutOfRangeBookingRequestDto, 1L));

            Assertions.assertEquals("Provided provided time slot is invalid", exception.getMessage());
        }

        @Test
        public void testExceptionIsThrownWhenCleaningProfessionalsBelongToDifferentVehicle() {

            when(scheduleConfigurationServiceImpl.getScheduleConfiguration()).thenReturn(
                    BookingFixture.configuration
            );

            when(customerRepository.findById(any())).thenReturn(
                    Optional.of(BookingFixture.customer)
            );

            when(cleaningProfessionalRepository.findByIdIn(any())).thenReturn(
                    List.of(
                            BookingFixture.cleaningProfessional,
                            BookingFixture.cleaningProfessional2
                    )
            );

            var exception = Assertions.assertThrows(
                    BookingException.class,
                    () -> bookingServiceImpl.create(BookingFixture.createEndOutOfRangeBookingRequestDto, 1L));

            Assertions.assertEquals("Provided cleaning professionals are not valid for this booking", exception.getMessage());
        }

        @Test
        public void testExceptionIsThrownWhenSlotIsNotAvailable() {

            when(scheduleConfigurationServiceImpl.getScheduleConfiguration()).thenReturn(
                    BookingFixture.configuration
            );

            when(customerRepository.findById(any())).thenReturn(
                    Optional.of(BookingFixture.customer)
            );

            when(bookedSlotRepository.findBookedCleanerSlotsInDateTimeRange(any(), any(),
                    any(), any(), anyList(), any())).thenReturn(
                    List.of(BookingFixture.bookedSlots)
            );

            var exception = Assertions.assertThrows(
                    BookingException.class,
                    () -> bookingServiceImpl.create(BookingFixture.createBookingRequestDto, 1L));

            Assertions.assertEquals("Slot not available", exception.getMessage());
        }
    }
}