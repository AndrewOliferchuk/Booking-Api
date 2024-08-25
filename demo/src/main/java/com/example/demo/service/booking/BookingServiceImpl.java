package com.example.demo.service.booking;

import com.example.demo.dto.booking.BookingRequestDto;
import com.example.demo.dto.booking.BookingResponseDto;
import com.example.demo.exception.EntityNotFoundException;
import com.example.demo.mapper.BookingMapper;
import com.example.demo.model.Booking;
import com.example.demo.model.enums.BookingStatus;
import com.example.demo.repository.BookingRepository;
import com.example.demo.service.notification.NotificationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final NotificationService notificationService;

    @Override
    public BookingResponseDto save(BookingRequestDto requestDto) {
        Booking booking = bookingMapper.toModel(requestDto);
        notificationService.notifyNewBooking(booking);
        return bookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    public List<BookingResponseDto> findByUserIdAndStatus(String status, Long userid) {
        BookingStatus bookingStatus = BookingStatus.getString(status);
        return bookingRepository.findByUserIdAndStatus(userid, bookingStatus)
                .stream()
                .map(bookingMapper::toDto)
                .toList();
    }

    @Override
    public BookingResponseDto getById(Long id) {
        Booking booking = findBookingById(id);
        return bookingMapper.toDto(booking);
    }

    @Override
    public BookingResponseDto updateById(Long id, BookingRequestDto requestDto) {
        Booking booking = findBookingById(id);
        bookingMapper.updateBooking(requestDto, booking);
        if (booking.getStatus().equals(BookingStatus.CANCELED)) {
            notificationService.notifyBookingCanceled(booking);
        }
        return bookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    public List<BookingResponseDto> getUserBookings(Long userId) {
        return bookingRepository.findBookingsByUserId(userId)
                .stream()
                .map(bookingMapper::toDto)
                .toList();
    }

    @Override
    public void delete(Long id) {
        bookingRepository.deleteById(id);
    }

    private Booking findBookingById(Long id) {
        return bookingRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't get booking by id: " + id));
    }
}
