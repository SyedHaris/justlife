package com.example.justlife.dto.response;

import java.time.LocalDate;

public record UpdateBookingResponseDto(Long id, LocalDate date, String startTime, String endTime) {
}
