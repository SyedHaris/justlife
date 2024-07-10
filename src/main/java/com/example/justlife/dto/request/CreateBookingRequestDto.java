package com.example.justlife.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public record CreateBookingRequestDto(
        @NotNull(message = "date can not be null")
        LocalDate date,
        @NotNull(message = "startTime can not be null")
        @Pattern(regexp = "([01][0-9]|2[0-3]):(00|30)", message = "startTime is not in correct format")
        String startTime,
        @NotNull(message = "duration can not be null")
        @Min(2)
        @Max(4)
        short duration,
        @Size(min = 1, max = 3, message = "cleaningProfessionals list must be at least 1 or at most 3")
        List<Long> cleaningProfessionals) {
}
