package com.example.demo.controller;

import com.example.demo.dto.booking.BookingRequestDto;
import com.example.demo.dto.booking.BookingResponseDto;
import com.example.demo.model.User;
import com.example.demo.service.booking.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Booking management",
        description = "Endpoints for managing bookings")
@RequiredArgsConstructor
@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @PreAuthorize("hasAnyRole('MANAGER','CUSTOMER')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new booking",
            description = "Save a new booking in repository")
    public BookingResponseDto saveBooking(@RequestBody @Valid BookingRequestDto requestDto) {
        return bookingService.save(requestDto);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER','CUSTOMER')")
    @Operation(summary = "Retrieve booking by ID",
            description = "Fetch the details of a specific "
                    + "booking using its unique identifier")
    public BookingResponseDto getById(@PathVariable Long id) {
        return bookingService.getById(id);
    }

    @GetMapping("/{userId}/{status}")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Get bookings by user ID and status",
            description = "Retrieves a list of bookings based on the user ID and status provided")
    public List<BookingResponseDto> findByUserIdAndStatus(@PathVariable String status,
                                                          @PathVariable Long userId) {
        return bookingService.findByUserIdAndStatus(status, userId);
    }

    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('MANAGER','CUSTOMER')")
    @Operation(summary = "Retrieve all bookings for the authenticated user",
            description = "Fetches a list of all bookings associated with "
                    + "the currently authenticated user ")
    @ResponseStatus(HttpStatus.OK)
    public List<BookingResponseDto> findAllByUserId(@AuthenticationPrincipal User user) {
        return bookingService.getUserBookings(user.getId());
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER','CUSTOMER')")
    @Operation(summary = "Update a booking by ID",
            description = "Updates the details of a booking specified by the given ID ")
    public BookingResponseDto updateBooking(@PathVariable @Positive Long id,
                                              @RequestBody @Valid BookingRequestDto requestDto) {
        return bookingService.updateById(id, requestDto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('MANAGER','CUSTOMER')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a booking by ID",
            description = "Deletes the booking specified by the given ID")
    public void deleteBooking(@PathVariable Long id) {
        bookingService.delete(id);
    }
}
