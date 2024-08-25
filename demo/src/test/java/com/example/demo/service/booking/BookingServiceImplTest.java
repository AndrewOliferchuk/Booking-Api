package com.example.demo.service.booking;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.example.demo.dto.booking.BookingRequestDto;
import com.example.demo.dto.booking.BookingResponseDto;
import com.example.demo.exception.EntityNotFoundException;
import com.example.demo.mapper.BookingMapper;
import com.example.demo.model.Booking;
import com.example.demo.model.enums.BookingStatus;
import com.example.demo.repository.BookingRepository;
import com.example.demo.service.notification.NotificationService;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @InjectMocks
    private BookingServiceImpl bookingService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private NotificationService notificationService;
    @Mock
    private BookingMapper bookingMapper;

    @Test
    @DisplayName("Verify create, method works")
    void save_ValidCreateRequestDto_returnResponseDto() {
        BookingRequestDto requestDto = new BookingRequestDto();
        requestDto.setCheckInDate(LocalDate.of(2024, 8, 15));
        requestDto.setCheckOutDate(LocalDate.of(2024, 8, 20));
        requestDto.setUserId(1L);
        requestDto.setAccommodationId(1L);
        requestDto.setStatus(BookingStatus.PENDING);

        Booking booking = new Booking();
        booking.setCheckInDate(requestDto.getCheckInDate());
        booking.setCheckOutDate(requestDto.getCheckOutDate());
        booking.setStatus(requestDto.getStatus());
        booking.setUserId(requestDto.getUserId());
        booking.setAccommodationId(requestDto.getAccommodationId());

        BookingResponseDto responseDto = new BookingResponseDto();
        responseDto.setId(1L);
        responseDto.setCheckInDate(booking.getCheckInDate());
        responseDto.setCheckOutDate(booking.getCheckOutDate());
        responseDto.setStatus(booking.getStatus());
        responseDto.setUserId(booking.getUserId());
        responseDto.setAccommodationId(booking.getAccommodationId());

        when(bookingMapper.toModel(requestDto)).thenReturn(booking);
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingMapper.toDto(booking)).thenReturn(responseDto);

        BookingResponseDto bookingResponseDto = bookingService.save(requestDto);

        assertThat(bookingResponseDto).isEqualTo(responseDto);
        verify(bookingMapper, times(1)).toModel(requestDto);
        verify(bookingRepository, times(1)).save(booking);
        verify(notificationService, times(1)).notifyNewBooking(booking);
        verify(bookingMapper, times(1)).toDto(booking);
        verifyNoMoreInteractions(bookingMapper, bookingRepository, notificationService);
    }

    @Test
    void findByUserIdAndStatus_correctValue_returnList() {
        Long userId = 1L;
        BookingStatus bookingStatus = BookingStatus.PENDING;

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setCheckInDate(LocalDate.of(2024, 8, 17));
        booking.setCheckOutDate(LocalDate.of(2024, 8, 26));
        booking.setStatus(bookingStatus);
        booking.setUserId(userId);
        booking.setAccommodationId(1L);

        BookingResponseDto responseDto = new BookingResponseDto();
        responseDto.setId(responseDto.getId());
        responseDto.setCheckInDate(booking.getCheckInDate());
        responseDto.setCheckOutDate(booking.getCheckOutDate());
        responseDto.setStatus(booking.getStatus());
        responseDto.setUserId(booking.getUserId());
        responseDto.setAccommodationId(booking.getAccommodationId());

        List<Booking> bookingList = List.of(booking);

        when(bookingRepository.findByUserIdAndStatus(userId, bookingStatus)).thenReturn(
                bookingList);
        when(bookingMapper.toDto(booking)).thenReturn(responseDto);

        List<BookingResponseDto> result = bookingService.findByUserIdAndStatus(
                String.valueOf(bookingStatus), userId);

        assertThat(result.get(0)).isEqualTo(responseDto);
        verify(bookingRepository, times(1))
                .findByUserIdAndStatus(userId, bookingStatus);
        verify(bookingMapper, times(1)).toDto(booking);
        verifyNoMoreInteractions(bookingMapper, bookingRepository);
    }

    @Test
    void getById_ValidFoundById_returnResponseDto() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setCheckInDate(LocalDate.of(2024, 8, 17));
        booking.setCheckOutDate(LocalDate.of(2024, 8, 26));
        booking.setStatus(BookingStatus.PENDING);
        booking.setUserId(1L);
        booking.setAccommodationId(1L);

        BookingResponseDto responseDto = new BookingResponseDto();
        responseDto.setId(responseDto.getId());
        responseDto.setCheckInDate(booking.getCheckInDate());
        responseDto.setCheckOutDate(booking.getCheckOutDate());
        responseDto.setStatus(booking.getStatus());
        responseDto.setUserId(booking.getUserId());
        responseDto.setAccommodationId(booking.getAccommodationId());

        when(bookingMapper.toDto(booking)).thenReturn(responseDto);
        when(bookingRepository.findById(responseDto.getId())).thenReturn(Optional.of(booking));

        BookingResponseDto result = bookingService.getById(responseDto.getId());

        assertThat(result).isEqualTo(responseDto);
        verify(bookingMapper, times(1)).toDto(booking);
        verify(bookingRepository, times(1)).findById(responseDto.getId());
        verifyNoMoreInteractions(bookingMapper, bookingRepository);
    }

    @Test
    @DisplayName("Update booking by ID - success")
    void updateById_validUpdateByIdAndRequest_returnUpdateResponse() {
        Long bookingId = 1L;

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setCheckInDate(LocalDate.of(2024, 8, 17));
        booking.setCheckOutDate(LocalDate.of(2024, 8, 26));
        booking.setStatus(BookingStatus.CANCELED);
        booking.setUserId(1L);
        booking.setAccommodationId(1L);

        BookingRequestDto requestDto = new BookingRequestDto();
        requestDto.setCheckInDate(LocalDate.of(2024, 8, 15));
        requestDto.setCheckOutDate(LocalDate.of(2024, 8, 20));
        requestDto.setUserId(1L);
        requestDto.setAccommodationId(1L);
        requestDto.setStatus(BookingStatus.CANCELED);

        BookingResponseDto responseDto = new BookingResponseDto();
        responseDto.setId(bookingId);
        responseDto.setCheckInDate(booking.getCheckInDate());
        responseDto.setCheckOutDate(booking.getCheckOutDate());
        responseDto.setStatus(booking.getStatus());
        responseDto.setUserId(booking.getUserId());
        responseDto.setAccommodationId(booking.getAccommodationId());

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        doNothing().when(bookingMapper).updateBooking(requestDto, booking);
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingMapper.toDto(booking)).thenReturn(responseDto);

        BookingResponseDto result = bookingService.updateById(bookingId, requestDto);

        assertThat(result).isEqualTo(responseDto);
        verify(bookingRepository).findById(bookingId);
        verify(bookingRepository).save(booking);
        verify(notificationService, times(1)).notifyBookingCanceled(booking);
        verify(bookingMapper).updateBooking(requestDto, booking);
        verify(bookingMapper).toDto(booking);
        verifyNoMoreInteractions(bookingRepository, bookingMapper, notificationService);
    }

    @Test
    void updateById_InvalidId_ThrowsEntityNotFoundException() {
        Long bookingId = 1L;

        BookingRequestDto requestDto = new BookingRequestDto();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            bookingService.updateById(bookingId, requestDto);
        });

        assertEquals("Can't get booking by id: " + bookingId,
                exception.getMessage());
        verify(bookingRepository).findById(bookingId);
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    void getUserBookings_userIsExist_Success() {
        Long userId = 1L;

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setCheckInDate(LocalDate.of(2024, 8, 17));
        booking.setCheckOutDate(LocalDate.of(2024, 8, 26));
        booking.setStatus(BookingStatus.PENDING);
        booking.setUserId(userId);
        booking.setAccommodationId(1L);

        BookingResponseDto responseDto = new BookingResponseDto();
        responseDto.setId(booking.getId());
        responseDto.setCheckInDate(booking.getCheckInDate());
        responseDto.setCheckOutDate(booking.getCheckOutDate());
        responseDto.setStatus(booking.getStatus());
        responseDto.setUserId(booking.getUserId());
        responseDto.setAccommodationId(booking.getAccommodationId());

        List<Booking> bookingList = List.of(booking);

        when(bookingRepository.findBookingsByUserId(userId)).thenReturn(bookingList);
        when(bookingMapper.toDto(booking)).thenReturn(responseDto);

        List<BookingResponseDto> result = bookingService.getUserBookings(userId);

        assertThat(result.get(0)).isEqualTo(responseDto);
        verify(bookingRepository, times(1)).findBookingsByUserId(userId);
        verify(bookingMapper, times(1)).toDto(booking);
        verifyNoMoreInteractions(bookingMapper, bookingRepository);
    }

    @Test
    void delete_mustDelete_WhenIdExist() {
        Long bookingId = 1L;
        bookingService.delete(bookingId);
        verify(bookingRepository, times(1)).deleteById(bookingId);
    }
}
