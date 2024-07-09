package com.example.justlife.service;

import com.example.justlife.dto.request.CreateBookingRequestDto;
import com.example.justlife.dto.response.CreateBookingResponseDto;

public interface BookingService {
    CreateBookingResponseDto create(CreateBookingRequestDto createBookingRequestDto, Long customerId);
}
