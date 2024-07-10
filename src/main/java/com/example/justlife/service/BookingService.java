package com.example.justlife.service;

import com.example.justlife.dto.request.CreateBookingRequestDto;
import com.example.justlife.dto.request.UpdateBookingRequestDto;
import com.example.justlife.dto.response.CreateBookingResponseDto;
import com.example.justlife.dto.response.UpdateBookingResponseDto;

public interface BookingService {
    CreateBookingResponseDto create(CreateBookingRequestDto createBookingRequestDto, Long customerId);

    UpdateBookingResponseDto update(UpdateBookingRequestDto updateBookingRequestDto, Long customerId, Long bookingId);

}
