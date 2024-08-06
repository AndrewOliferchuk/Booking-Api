package com.example.demo.dto.booking;

import com.example.demo.model.enums.BookingStatus;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Data;

@Data
public class BookingRequestDto {
    @NotNull
    @Future
    private LocalDate checkInDate;
    @NotNull
    @Future
    private LocalDate checkOutDate;
    @NotNull
    private Long accommodationId;
    @NotNull
    private Long userId;
    @NotNull
    private BookingStatus status;
}
