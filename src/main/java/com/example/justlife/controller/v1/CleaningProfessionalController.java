package com.example.justlife.controller.v1;

import com.example.justlife.dto.response.GetCleaningProfessionalsResponseDto;
import com.example.justlife.service.CleaningProfessionalService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/v1/cleaners")
public class CleaningProfessionalController {
    private final CleaningProfessionalService cleaningProfessionalService;

    public CleaningProfessionalController(CleaningProfessionalService cleaningProfessionalService) {
        this.cleaningProfessionalService = cleaningProfessionalService;
    }

    @GetMapping
    @ResponseStatus(value = HttpStatus.OK)
    public List<GetCleaningProfessionalsResponseDto> getAvailability(
            @RequestParam("date") @NotNull(message = "date can not be null") LocalDate date,
            @RequestParam(value = "startTime", required = false) @Pattern(regexp = "([01][0-9]|2[0-3]):(00|30)", message = "startTime is not in correct format")
            String startTime,
            @RequestParam(value = "duration", required = false) Optional<Short> duration,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") @Max(50) int size) {
        return this.cleaningProfessionalService.getAvailability(date, startTime, duration, page, size);
    }

}
