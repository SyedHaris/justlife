package com.example.justlife.functional;

import com.example.justlife.service.CleaningProfessionalServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static com.example.justlife.fixture.BookingFixture.*;
import static com.example.justlife.fixture.CleaningProfessionalFixture.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CleaningProfessionalControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CleaningProfessionalServiceImpl service;

    @Test
    void shouldGetAvailableCleaningProfessionals() throws Exception {
        when(service.getAvailability(date, null, Optional.empty(), 0, 10)).thenReturn(
                Collections.singletonList(getCleaningProfessionalsResponseDto)
        );

        this.mockMvc.perform(get("/api/v1/cleaners")
                        .param("date", date.toString())
                        .param("page", "0")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*.name").value("test-cleaning-professional"))
                .andExpect(jsonPath("$.*.availableSlots").exists());
    }

    @Test
    void shouldNotGetAvailableCleaningProfessionalsWhenParamsAreInvalid() throws Exception {
        when(service.getAvailability(date, null, Optional.empty(), 0, 10)).thenReturn(
                Collections.singletonList(getCleaningProfessionalsResponseDto)
        );

        this.mockMvc.perform(get("/api/v1/cleaners")
                        .param("page", "0")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }
}
