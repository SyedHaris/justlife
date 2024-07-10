package com.example.justlife.functional;

import com.example.justlife.fixture.BookingFixture;
import com.example.justlife.service.BookingServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BookingControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingServiceImpl service;

    @Test
    void shouldCreateBooking() throws Exception {
        when(service.create(any(),any())).thenReturn(
                BookingFixture.createBookingResponseDto
        );

        this.mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(BookingFixture.createBookingRequestDto))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bookingId").value(10));
    }

    @Test
    void shouldNotCreateBookingWhenParamsAreInvalid() throws Exception {
        when(service.create(any(),any())).thenReturn(
                BookingFixture.createBookingResponseDto
        );

        this.mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(BookingFixture.createInvalidParamsBookingRequestDto))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    void shouldUpdateBooking() throws Exception {
        when(service.update(any(),any(), any())).thenReturn(
                BookingFixture.updateBookingResponseDto
        );

        this.mockMvc.perform(put("/api/v1/bookings/100")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(BookingFixture.updateBookingRequestDto))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.startTime").value("9:30"))
                .andExpect(jsonPath("$.endTime").value("11:30"));
    }

    @Test
    void shouldNotUpdateBookingWhenParamsAreInvalid() throws Exception {
        when(service.update(any(),any(), any())).thenReturn(
                BookingFixture.updateBookingResponseDto
        );

        this.mockMvc.perform(put("/api/v1/bookings/100")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(BookingFixture.updateInvalidParamsBookingRequestDto))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists());
    }
}