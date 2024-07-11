package com.example.justlife.fixture;

import com.example.justlife.dto.response.GetCleaningProfessionalsResponseDto;
import com.example.justlife.model.CleaningProfessional;
import com.example.justlife.model.Vehicle;

import java.util.List;

public class CleaningProfessionalFixture {
    public static final Vehicle vehicle = new Vehicle(1L, "ABC-123", "test-driver", "test-make", "test-model", "2024");
    public static final Vehicle vehicle2 = new Vehicle(2L, "ABC-145", "test-driver-2", "test-make", "test-model", "2024");
    public static final CleaningProfessional cleaningProfessional = new CleaningProfessional(1L, "test-cleaning-professional-1", "test2@example.com", "example.com", 4.5, vehicle, null);
    public static final CleaningProfessional cleaningProfessional2 = new CleaningProfessional(1L, "test-cleaning-professional-2", "test3@example.com", "example.com", 4.0, vehicle2, null);
    public static final GetCleaningProfessionalsResponseDto getCleaningProfessionalsResponseDto = new GetCleaningProfessionalsResponseDto(1L, "test-cleaning-professional", "test@example.com", 4.5, "example.com", List.of("08:00-22:00"));
}
