package com.example.demo.service.booking;

import com.example.demo.dto.booking.BookingRequestDto;
import com.example.demo.dto.booking.BookingResponseDto;
import java.util.List;

public interface BookingService {
    BookingResponseDto save(BookingRequestDto requestDto);

    List<BookingResponseDto> findByUserIdAndStatus(String status, Long userid);

    BookingResponseDto getById(Long id);

    BookingResponseDto updateById(Long id, BookingRequestDto requestDto);

    List<BookingResponseDto> getUserBookings(Long userId);

    void delete(Long id);
}
