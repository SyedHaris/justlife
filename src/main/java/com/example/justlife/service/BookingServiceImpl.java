package com.example.justlife.service;

import com.example.justlife.dto.request.CreateBookingRequestDto;
import com.example.justlife.dto.request.UpdateBookingRequestDto;
import com.example.justlife.dto.response.CreateBookingResponseDto;
import com.example.justlife.dto.response.UpdateBookingResponseDto;
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
        if (isSlotNotAvailable(createBookingRequestDto.date(), startTime, endTime,
                                createBookingRequestDto.cleaningProfessionals(), scheduleConfiguration, null))
            throw new BookingException("Slot not available");

        // Create booking
        var booking = saveBooking(createBookingRequestDto, cleaningProfessionals, startTime, endTime, customer);
        saveBookedSlots(createBookingRequestDto, cleaningProfessionals, startTime, endTime, booking, scheduleConfiguration.getBreakDurationMinutes());

        return new CreateBookingResponseDto(booking.getId());
    }

    @Transactional
    @Override
    public UpdateBookingResponseDto update(UpdateBookingRequestDto updateBookingRequestDto, Long customerId, Long bookingId) {
        // Check booking exists
        var booking = bookingRepository.findFirstByIdAndCustomerId(bookingId, customerId)
                                       .orElseThrow(() -> new BookingException("Booking does not exist"));
        var scheduleConfiguration = getScheduleConfiguration();

        // Check not holiday
        if (isHoliday(updateBookingRequestDto.date(), scheduleConfiguration))
            throw new BookingException("Booking can't be created on a holiday");

        // start time and end time are not out of range
        var startTime = helper.convertStringToLocalTime(updateBookingRequestDto.startTime());
        var endTime = helper.calculateEndTimeFromDurationAndStartTime(startTime, updateBookingRequestDto.duration());
        if (isTimeOutOfRange(startTime, endTime, scheduleConfiguration))
            throw new BookingException("Provided provided time slot is invalid");

        // Check available slot excluding current booking
        var cleaningProfessionalIds = booking.getCleaningProfessionals()
                                             .stream()
                                             .map(CleaningProfessional::getId)
                                             .toList();
        if (isSlotNotAvailable(updateBookingRequestDto.date(), startTime, endTime,
                                cleaningProfessionalIds, scheduleConfiguration, booking.getId()))
            throw new BookingException("Slot not available");

        // Update booking
        booking.setDate(updateBookingRequestDto.date());
        booking.setStartTime(startTime);
        booking.setEndTime(endTime);
        bookingRepository.save(booking);

        var bookingSlots = bookedSlotRepository.findByBookingId(booking.getId());
        bookingSlots.forEach(b -> {
            b.setDate(updateBookingRequestDto.date());
            b.setStartTime(startTime);
            b.setEndTime(endTime);
        });
        bookedSlotRepository.saveAll(bookingSlots);

        return new UpdateBookingResponseDto(booking.getId(), booking.getDate(), booking.getStartTime().toString(), booking.getEndTime().toString());
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

        return startTime.isBefore(configuredStartTime) || startTime.isAfter(configuredEndTime) || endTime.isAfter(configuredEndTime);
    }

    public boolean isSlotNotAvailable(LocalDate date, LocalTime startTime, LocalTime endTime,
                                      List<Long> cleaningProfessionalIds, ScheduleConfiguration scheduleConfiguration, Long id) {
        // Case for handling overlapping tasks
        startTime = startTime.minusHours(1);
        endTime = endTime.plusHours(1);

        return bookedSlotRepository.findBookedCleanerSlotsInDateTimeRange(date, startTime, endTime,
                                                                            cleaningProfessionalIds, id).size() > 0;
    }
}
