package com.example.demo.dto.booking;

import com.example.demo.model.enums.BookingStatus;
import java.time.LocalDate;
import lombok.Data;

@Data
public class BookingResponseDto {
    private Long id;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Long accommodationId;
    private Long userId;
    private BookingStatus status;
}
