package com.example.justlife.service;

import com.example.justlife.dto.request.CreateBookingRequestDto;
import com.example.justlife.dto.request.UpdateBookingRequestDto;
import com.example.justlife.dto.response.CreateBookingResponseDto;
import com.example.justlife.dto.response.UpdateBookingResponseDto;
import com.example.justlife.model.BookedSlot;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;


public interface BookingService {
    CreateBookingResponseDto create(CreateBookingRequestDto requestDto, Long customerId);

    UpdateBookingResponseDto update(UpdateBookingRequestDto requestDto, Long customerId, Long bookingId);

    List<BookedSlot> getBookedSlots(LocalDate date, LocalTime startTime, LocalTime endTime,
                                    List<Long> cleaningProfessionalIds, Long id);
}
