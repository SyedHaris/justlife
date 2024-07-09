package com.example.justlife.service;

import com.example.justlife.dto.request.CreateBookingRequestDto;
import com.example.justlife.dto.response.CreateBookingResponseDto;
import com.example.justlife.enums.BookingStatus;
import com.example.justlife.exception.BookingException;
import com.example.justlife.exception.CustomerNotFoundException;
import com.example.justlife.model.BookedSlot;
import com.example.justlife.model.Booking;
import com.example.justlife.model.CleaningProfessional;
import com.example.justlife.model.Customer;
import com.example.justlife.model.ScheduleConfiguration;
import com.example.justlife.repository.BookedSlotRepository;
import com.example.justlife.repository.BookingRepository;
import com.example.justlife.repository.CleaningProfessionalRepository;
import com.example.justlife.repository.CustomerRepository;
import com.example.justlife.repository.ScheduleConfigurationRepository;
import com.example.justlife.util.Helper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class BookingServiceImpl implements BookingService {
    private final ScheduleConfigurationRepository scheduleConfigurationRepository;
    private final CleaningProfessionalRepository cleaningProfessionalRepository;
    private final BookedSlotRepository bookedSlotRepository;
    private final BookingRepository bookingRepository;
    private final CustomerRepository customerRepository;
    private final Helper helper;

    public BookingServiceImpl(ScheduleConfigurationRepository scheduleConfigurationRepository,
                              CleaningProfessionalRepository cleaningProfessionalRepository, BookedSlotRepository bookedSlotRepository, BookingRepository bookingRepository, CustomerRepository customerRepository, Helper helper) {
        this.scheduleConfigurationRepository = scheduleConfigurationRepository;
        this.cleaningProfessionalRepository = cleaningProfessionalRepository;
        this.bookedSlotRepository = bookedSlotRepository;
        this.bookingRepository = bookingRepository;
        this.customerRepository = customerRepository;
        this.helper = helper;
    }

    @Transactional
    @Override
    public CreateBookingResponseDto create(CreateBookingRequestDto createBookingRequestDto,
                                           Long customerId) {
        var scheduleConfiguration = getScheduleConfiguration();
        var customer = customerRepository.findById(customerId).orElseThrow(
                () -> new CustomerNotFoundException("Customer doesn't exist")
        );

        // Check not holiday
        if (isHoliday(createBookingRequestDto.date(), scheduleConfiguration))
            throw new BookingException("Booking can't be created on a holiday");

        // Check all cleaning professionals belong to the same vehicle
        var cleaningProfessionals = cleaningProfessionalRepository.findByIdIn(createBookingRequestDto.cleaningProfessionals());
        if (cleaningProfessionals.size() > 1 && isVehicleNotValid(cleaningProfessionals))
            throw new BookingException("Provided cleaning professionals are not valid for this booking");

        // start time and end time are not out of range
        var startTime = helper.convertStringToLocalTime(createBookingRequestDto.startTime());
        var endTime = helper.calculateEndTimeFromDurationAndStartTime(startTime, createBookingRequestDto.duration());
        if (isTimeOutOfRange(startTime, endTime, scheduleConfiguration))
            throw new BookingException("Provided provided time slot is invalid");

        // Check slot is not in booked slots
        if (isSlotNotAvailable(createBookingRequestDto.date(), startTime, endTime, createBookingRequestDto.cleaningProfessionals(), scheduleConfiguration))
            throw new BookingException("Slot not available");

        // Create booking
        var booking = saveBooking(createBookingRequestDto, cleaningProfessionals, startTime, endTime, customer);
        saveBookedSlots(createBookingRequestDto, cleaningProfessionals, startTime, endTime, booking);

        return new CreateBookingResponseDto(booking.getId());
    }

    private void saveBookedSlots(CreateBookingRequestDto createBookingRequestDto, List<CleaningProfessional> cleaningProfessionals, LocalTime startTime, LocalTime endTime, Booking booking) {
        var bookedSlots = new ArrayList<BookedSlot>();

        cleaningProfessionals.forEach(cp -> {
            bookedSlots.add(
                    BookedSlot.builder()
                            .date(createBookingRequestDto.date())
                            .startTime(startTime)
                            .endTime(endTime)
                            .booking(booking)
                            .cleaningProfessional(cp)
                            .build()
            );
        });

        bookedSlotRepository.saveAll(bookedSlots);
    }

    private Booking saveBooking(CreateBookingRequestDto createBookingRequestDto, List<CleaningProfessional> cleaningProfessionals, LocalTime startTime, LocalTime endTime, Customer customer) {
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

    private ScheduleConfiguration getScheduleConfiguration() {
        return scheduleConfigurationRepository.findAll()
                                              .stream()
                                              .findFirst()
                                              .orElseThrow(() -> new BookingException("Booking can't be created"));
    }

    private boolean isHoliday(LocalDate date, ScheduleConfiguration scheduleConfiguration) {
        return scheduleConfiguration.getHoliday().equals(date.getDayOfWeek());
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

        return startTime.isBefore(configuredStartTime) || endTime.isAfter(configuredEndTime);
    }

    public boolean isSlotNotAvailable(LocalDate date, LocalTime startTime, LocalTime endTime, List<Long> cleaningProfessionalIds, ScheduleConfiguration scheduleConfiguration) {
        // Cater for break duration
        var startTimeWithBreak = startTime.minusMinutes(((long) scheduleConfiguration.getBreakDurationMinutes()));
        if (!startTimeWithBreak.isBefore(scheduleConfiguration.getStartTime()))
            startTime = startTimeWithBreak;
        return bookedSlotRepository.findBookedCleanerSlotsInDateTimeRange(date, startTime, endTime, cleaningProfessionalIds).size() > 0;
    }
}
