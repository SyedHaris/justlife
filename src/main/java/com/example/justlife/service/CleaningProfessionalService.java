package com.example.justlife.service;

import com.example.justlife.dto.response.GetCleaningProfessionalsResponseDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CleaningProfessionalService {

    List<GetCleaningProfessionalsResponseDto> getAvailability(LocalDate date, String startTime, Optional<Short> duration, int page, int size);

}
