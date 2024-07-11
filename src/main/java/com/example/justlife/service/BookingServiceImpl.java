package com.example.justlife.service;

import com.example.justlife.dto.request.CreateBookingRequestDto;
import com.example.justlife.dto.request.UpdateBookingRequestDto;
import com.example.justlife.dto.response.CreateBookingResponseDto;
import com.example.justlife.dto.response.UpdateBookingResponseDto;
import com.example.justlife.enums.BookingStatus;
import com.example.justlife.exception.BookingException;
import com.example.justlife.exception.CustomerNotFoundException;
import com.example.justlife.exception.HolidayException;
import com.example.justlife.model.BookedSlot;
import com.example.justlife.model.Booking;
import com.example.justlife.model.CleaningProfessional;
import com.example.justlife.model.Customer;
import com.example.justlife.model.ScheduleConfiguration;
import com.example.justlife.repository.BookedSlotRepository;
import com.example.justlife.repository.BookingRepository;
import com.example.justlife.repository.CleaningProfessionalRepository;
import com.example.justlife.repository.CustomerRepository;
import com.example.justlife.util.Helper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.example.justlife.exception.BookingException.*;

@Service
public class BookingServiceImpl implements BookingService {
    private final ScheduleConfigurationService scheduleConfigurationService;
    private final CleaningProfessionalRepository cleaningProfessionalRepository;
    private final BookedSlotRepository bookedSlotRepository;
    private final BookingRepository bookingRepository;
    private final CustomerRepository customerRepository;
    private final Helper helper;

    public BookingServiceImpl(ScheduleConfigurationService scheduleConfigurationService,
                              CleaningProfessionalRepository cleaningProfessionalRepository, BookedSlotRepository bookedSlotRepository, BookingRepository bookingRepository, CustomerRepository customerRepository, Helper helper) {
        this.scheduleConfigurationService = scheduleConfigurationService;
        this.cleaningProfessionalRepository = cleaningProfessionalRepository;
        this.bookedSlotRepository = bookedSlotRepository;
        this.bookingRepository = bookingRepository;
        this.customerRepository = customerRepository;
        this.helper = helper;
    }

    @Transactional
    @Override
    public CreateBookingResponseDto create(CreateBookingRequestDto requestDto,
                                           Long customerId) {
        var scheduleConfiguration = scheduleConfigurationService.getScheduleConfiguration();
        var customer = customerRepository.findById(customerId).orElseThrow(
                CustomerNotFoundException::new
        );

        // Check not holiday
        if (scheduleConfigurationService.isHoliday(requestDto.date(), scheduleConfiguration))
            throw new HolidayException();

        // Check all cleaning professionals belong to the same vehicle
        var cleaningProfessionals = cleaningProfessionalRepository.findByIdIn(requestDto.cleaningProfessionals());
        if (cleaningProfessionals.size() > 1 && isVehicleNotValid(cleaningProfessionals))
            throw new BookingException(CLEANING_PROFESSIONALS_INVALID);

        // start time and end time are not out of range
        var startTime = helper.convertStringToLocalTime(requestDto.startTime());
        var endTime = helper.calculateEndTimeFromDurationAndStartTime(startTime, requestDto.duration());
        if (isTimeOutOfRange(startTime, endTime, scheduleConfiguration))
            throw new BookingException(TIME_SLOT_NOT_VALID);

        // Check slot is not in booked slots
        if (getBookedSlots(requestDto.date(), startTime, endTime,
                                requestDto.cleaningProfessionals(), null).size() > 0)
            throw new BookingException(SLOT_NOT_AVAILABLE);

        // Create booking
        var booking = saveBooking(requestDto, cleaningProfessionals, startTime, endTime, customer);
        saveBookedSlots(requestDto, cleaningProfessionals, startTime, endTime, booking, scheduleConfiguration.getBreakDurationMinutes());

        return new CreateBookingResponseDto(booking.getId());
    }

    @Transactional
    @Override
    public UpdateBookingResponseDto update(UpdateBookingRequestDto requestDto, Long customerId, Long bookingId) {
        // Check booking exists
        var booking = bookingRepository.findFirstByIdAndCustomerId(bookingId, customerId)
                                       .orElseThrow(BookingException::new);
        var scheduleConfiguration = scheduleConfigurationService.getScheduleConfiguration();

        // Check not holiday
        if (scheduleConfigurationService.isHoliday(requestDto.date(), scheduleConfiguration))
            throw new HolidayException();

        // start time and end time are not out of range
        var startTime = helper.convertStringToLocalTime(requestDto.startTime());
        var endTime = helper.calculateEndTimeFromDurationAndStartTime(startTime, requestDto.duration());
        if (isTimeOutOfRange(startTime, endTime, scheduleConfiguration))
            throw new BookingException(TIME_SLOT_NOT_VALID);

        // Check available slot excluding current booking
        var cleaningProfessionalIds = booking.getCleaningProfessionals()
                                             .stream()
                                             .map(CleaningProfessional::getId)
                                             .toList();
        if (getBookedSlots(requestDto.date(), startTime, endTime,
                                cleaningProfessionalIds, booking.getId()).size() > 0)
            throw new BookingException(SLOT_NOT_AVAILABLE);

        // Update booking
        booking.setDate(requestDto.date());
        booking.setStartTime(startTime);
        booking.setEndTime(endTime);
        bookingRepository.save(booking);

        var bookingSlots = bookedSlotRepository.findByBookingId(booking.getId());
        bookingSlots.forEach(b -> {
            b.setDate(requestDto.date());
            b.setStartTime(startTime);
            b.setEndTime(endTime.plusMinutes(scheduleConfiguration.getBreakDurationMinutes()));
        });
        bookedSlotRepository.saveAll(bookingSlots);

        return new UpdateBookingResponseDto(booking.getId(), booking.getDate(), booking.getStartTime().toString(), booking.getEndTime().toString());
    }

    @Override
    public List<BookedSlot> getBookedSlots(LocalDate date, LocalTime startTime, LocalTime endTime,
                                           List<Long> cleaningProfessionalIds, Long id) {
        // Case for handling overlapping tasks
        var startTimeWithOverlap = startTime.minusHours(1);
        var endTimeWithOverlap = endTime.plusHours(1);

        return bookedSlotRepository.findBookedCleanerSlotsInDateTimeRange(date, startTimeWithOverlap, endTimeWithOverlap,
                startTime, cleaningProfessionalIds, id);
    }

    private void saveBookedSlots(CreateBookingRequestDto createBookingRequestDto,
                                 List<CleaningProfessional> cleaningProfessionals,
                                 LocalTime startTime, LocalTime endTime, Booking booking, Integer breakDurationMinutes) {
        var bookedSlots = new ArrayList<BookedSlot>();

        cleaningProfessionals.forEach(cp -> {
            bookedSlots.add(
                    BookedSlot.builder()
                            .date(createBookingRequestDto.date())
                            .startTime(startTime)
                            .endTime(endTime.plusMinutes(breakDurationMinutes))
                            .booking(booking)
                            .cleaningProfessional(cp)
                            .build()
            );
        });

        bookedSlotRepository.saveAll(bookedSlots);
    }

    private Booking saveBooking(CreateBookingRequestDto createBookingRequestDto,
                                List<CleaningProfessional> cleaningProfessionals,
                                LocalTime startTime, LocalTime endTime, Customer customer) {
        var booking = Booking.builder()
                                   .customer(customer)
                                   .status(BookingStatus.PENDING)
                                   .date(createBookingRequestDto.date())
                                   .startTime(startTime)
                                   .endTime(endTime)
                                   .cleaningProfessionals(new HashSet<>(cleaningProfessionals))
                                   .build();
        return bookingRepository.save(booking);
    }

    private boolean isVehicleNotValid(List<CleaningProfessional> cleaningProfessionals) {
        Set<Long> vehicleId = new HashSet<>();
        cleaningProfessionals.forEach(cp -> {
            vehicleId.add(cp.getVehicle().getId());
        });
        return vehicleId.size() != 1;
    }

    private boolean isTimeOutOfRange(LocalTime startTime, LocalTime endTime, ScheduleConfiguration scheduleConfiguration) {
        LocalTime configuredStartTime = scheduleConfiguration.getStartTime();
        LocalTime configuredEndTime = scheduleConfiguration.getEndTime();

        return startTime.isBefore(configuredStartTime) || startTime.isAfter(configuredEndTime) || endTime.isAfter(configuredEndTime);
    }
}
