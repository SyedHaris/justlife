package com.example.justlife.controller.v1;

import com.example.justlife.dto.request.CreateBookingRequestDto;
import com.example.justlife.dto.request.UpdateBookingRequestDto;
import com.example.justlife.dto.response.CreateBookingResponseDto;
import com.example.justlife.dto.response.UpdateBookingResponseDto;
import com.example.justlife.service.BookingService;
import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/bookings")
public class BookingController {
    private final BookingService bookingService;
    // In real scenario this would be replaced by customer id extracted from token
    private static final Long CUSTOMER_ID = 1L;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public CreateBookingResponseDto create(
            @RequestBody @Valid CreateBookingRequestDto createBookingRequestDto) {
        return bookingService.create(createBookingRequestDto, CUSTOMER_ID);
    }

    @PutMapping(path = "/{id}")
    @ResponseStatus(value = HttpStatus.OK)
    public UpdateBookingResponseDto create(
            @PathParam("id") Long bookingId,
            @RequestBody @Valid UpdateBookingRequestDto updateBookingRequestDto) {
        return bookingService.update(updateBookingRequestDto, CUSTOMER_ID, bookingId);
    }
}
