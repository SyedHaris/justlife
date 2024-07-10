package com.example.justlife.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record GetCleaningProfessionalsResponseDto(Long id, String name, String email, Double rating, String image_url, List<String> availableSlots) {
}
