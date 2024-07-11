package com.example.justlife.service;

import com.example.justlife.dto.response.GetCleaningProfessionalsResponseDto;
import com.example.justlife.model.BookedSlot;
import com.example.justlife.model.CleaningProfessional;
import com.example.justlife.model.ScheduleConfiguration;
import com.example.justlife.repository.BookedSlotRepository;
import com.example.justlife.repository.CleaningProfessionalRepository;
import com.example.justlife.util.Helper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class CleaningProfessionalServiceImpl implements  CleaningProfessionalService {
    private final ScheduleConfigurationService scheduleConfigurationService;
    private final CleaningProfessionalRepository cleaningProfessionalRepository;
    private final BookingService bookingService;
    private final Helper helper;
    private final BookedSlotRepository bookedSlotRepository;

    public CleaningProfessionalServiceImpl(ScheduleConfigurationService scheduleConfigurationService,
                                           CleaningProfessionalRepository cleaningProfessionalRepository,
                                           BookingService bookingService,
                                           Helper helper,
                                           BookedSlotRepository bookedSlotRepository) {
        this.scheduleConfigurationService = scheduleConfigurationService;
        this.cleaningProfessionalRepository = cleaningProfessionalRepository;
        this.bookingService = bookingService;
        this.helper = helper;
        this.bookedSlotRepository = bookedSlotRepository;
    }

    @Override
    public List<GetCleaningProfessionalsResponseDto> getAvailability(LocalDate date, String startTime, Optional<Short> duration, int page, int size) {
        var cleaningProfessionalsResponse = new ArrayList<GetCleaningProfessionalsResponseDto>();
        var pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        // Check not holiday
        var scheduleConfiguration = scheduleConfigurationService.getScheduleConfiguration();
        if (scheduleConfigurationService.isHoliday(date, scheduleConfiguration)) {
            return Collections.emptyList();
        }
        if (startTime != null && (duration.isPresent() && duration.get() >= 2 && duration.get() <= 4)) {
            var convertedStartTime = helper.convertStringToLocalTime(startTime);
            var endTime = helper.calculateEndTimeFromDurationAndStartTime(convertedStartTime, duration.get());
            var bookedSlots = bookingService.getBookedSlots(date, convertedStartTime, endTime, null, null);
            var cleanerProfessionalIds = bookedSlots.stream()
                                                   .map((bks) -> bks.getCleaningProfessional()
                                                   .getId())
                                                   .toList();
            var cleaningProfessionals = cleaningProfessionalRepository.findByIdNotIn(cleanerProfessionalIds, pageable);

            cleaningProfessionals
                    .forEach(cp -> {
                        cleaningProfessionalsResponse.add(
                                new GetCleaningProfessionalsResponseDto(cp.getId(), cp.getName(),
                                        cp.getEmail(), cp.getRating(),
                                        cp.getImageUrl(), null)
                        );
                    });
        }
        else {
            // Get all cleaning professionals sort by name asc and limit 10
            var cleaningProfessionals = cleaningProfessionalRepository.findAll(
                    pageable)
                    .getContent();
            // Calculate available slots
            cleaningProfessionals
                    .forEach(cp -> {
                        cleaningProfessionalsResponse.add(
                                new GetCleaningProfessionalsResponseDto(cp.getId(), cp.getName(),
                                        cp.getEmail(), cp.getRating(),
                                        cp.getImageUrl(), calculateAvailableSlots(cp, date, scheduleConfiguration))
                        );
                    });

        }
        return cleaningProfessionalsResponse;
    }

    private List<String> calculateAvailableSlots(CleaningProfessional cleaningProfessional, LocalDate date, ScheduleConfiguration scheduleConfiguration) {
        var availableSlots = new ArrayList<String>();
        var bookedSlots = bookedSlotRepository.findByCleaningProfessionalIdAndDate(cleaningProfessional.getId(), date);
        if (bookedSlots.isEmpty())
            return Collections.singletonList(scheduleConfiguration.getStartTime() + ":" + scheduleConfiguration.getEndTime());
        var previousEndTime = scheduleConfiguration.getStartTime();
        var scheduleEndTime = scheduleConfiguration.getEndTime();
        for (BookedSlot bks : bookedSlots) {
            if (previousEndTime.equals(bks.getStartTime()))
                continue;
            var currentStartTime = previousEndTime;
            while (!previousEndTime.equals(bks.getStartTime()) || previousEndTime.equals(scheduleEndTime)) {
                previousEndTime = previousEndTime.plusMinutes(30);
            }
            availableSlots.add(currentStartTime + "-" + previousEndTime);
            previousEndTime = bks.getEndTime();
        }
        if (previousEndTime.isBefore(scheduleEndTime))
            availableSlots.add(previousEndTime + "-" + scheduleEndTime);
        return availableSlots;
    }
}
