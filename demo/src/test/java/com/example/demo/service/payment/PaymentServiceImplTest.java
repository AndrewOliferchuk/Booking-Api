package com.example.demo.service.payment;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.example.demo.dto.payment.PaymentResponseDto;
import com.example.demo.exception.EntityNotFoundException;
import com.example.demo.mapper.PaymentMapper;
import com.example.demo.model.Accommodation;
import com.example.demo.model.Booking;
import com.example.demo.model.Payment;
import com.example.demo.model.enums.BookingStatus;
import com.example.demo.model.enums.PaymentStatus;
import com.example.demo.repository.AccommodationRepository;
import com.example.demo.repository.BookingRepository;
import com.example.demo.repository.PaymentRepository;
import com.example.demo.service.notification.NotificationService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {
    public static final String SESSION_ID = "session_id";
    public static final String SESSION_URL = "https://example.com/session_url";

    @InjectMocks
    private PaymentServiceImpl paymentService;
    @Mock
    private PaymentMapper paymentMapper;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private StripePaymentService stripePaymentService;
    @Mock
    private NotificationService notificationService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private AccommodationRepository accommodationRepository;

    @Test
    void createPayment_bookingIdIsNotExist_throwsException() {
        Long bookingId = 1L;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            paymentService.createPayment(bookingId);
        });

        assertEquals("Can't found booking by id: " + bookingId, exception.getMessage());
        verify(bookingRepository).findById(bookingId);
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    void createPayment_accommodationIdIsNotExist_throwsException() {
        Long bookingId = 1L;
        Long accommodationId = 2L;

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setAccommodationId(accommodationId);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(accommodationRepository.findById(accommodationId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            paymentService.createPayment(bookingId);
        });

        assertEquals("Can't found accommodation by id: "
                + accommodationId, exception.getMessage());
        verify(bookingRepository).findById(bookingId);
        verify(accommodationRepository).findById(accommodationId);
        verifyNoMoreInteractions(accommodationRepository);
    }

    @Test
    void getPaymentsByUserId_whenUserIdExists_responseDto() {
        Payment payment = new Payment();
        payment.setId(1L);
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setAmountToPay(BigDecimal.valueOf(100));

        PaymentResponseDto responseDto = new PaymentResponseDto();
        responseDto.setId(payment.getId());
        responseDto.setStatus(payment.getPaymentStatus());
        responseDto.setAmountToPay(payment.getAmountToPay());

        List<PaymentResponseDto> result = List.of(responseDto);

        Long userId = 1L;
        when(paymentRepository.findAllByBookingUserId(userId)).thenReturn(List.of(payment));
        when(paymentMapper.toDto(payment)).thenReturn(responseDto);

        List<PaymentResponseDto> actual = paymentService.getPaymentsByUserId(userId);

        assertThat(actual).isEqualTo(result);
        verify(paymentRepository,times(1)).findAllByBookingUserId(userId);
        verify(paymentMapper,times(1)).toDto(payment);
        verifyNoMoreInteractions(paymentRepository, paymentMapper);
    }

    @Test
    void handlePaymentSuccess_sessionIsNotExist_EntityNotFoundException() {
        String sessionId = "dfgdgg/egwr";

        when(paymentRepository.findBySessionId(sessionId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            paymentService.handlePaymentSuccess(sessionId);
        });

        assertEquals("Can't find payment with session id: "
                + sessionId, exception.getMessage());
        verify(paymentRepository).findBySessionId(sessionId);
        verifyNoMoreInteractions(paymentRepository);
    }

    @Test
    void handlePaymentSuccess_updatesPaymentStatusAndSendsNotification_Success() {
        String sessionId = "test";

        Payment payment = new Payment();
        payment.setId(1L);
        payment.setSessionId(sessionId);
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setAmountToPay(BigDecimal.valueOf(100));

        when(paymentRepository.findBySessionId(sessionId)).thenReturn(Optional.of(payment));
        doNothing().when(notificationService).notifyPaymentSuccess(payment);
        when(paymentRepository.save(payment)).thenReturn(payment);

        paymentService.handlePaymentSuccess(sessionId);

        assertEquals(PaymentStatus.PAID, payment.getPaymentStatus());
        verify(paymentRepository, times(1)).findBySessionId(sessionId);
        verify(notificationService, times(1)).notifyPaymentSuccess(payment);
        verify(paymentRepository, times(1)).save(payment);
        verifyNoMoreInteractions(paymentRepository, notificationService);
    }

    @Test
    void handlePaymentCancel_updatesPaymentStatusAndSendsNotification_Success() {
        String sessionId = "test";

        Payment payment = new Payment();
        payment.setId(1L);
        payment.setSessionId(sessionId);
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setAmountToPay(BigDecimal.valueOf(100));

        when(paymentRepository.findBySessionId(sessionId)).thenReturn(Optional.of(payment));
        doNothing().when(notificationService).notifyPaymentCancelled(payment);
        when(paymentRepository.save(payment)).thenReturn(payment);

        paymentService.handlePaymentCancel(sessionId);

        assertEquals(PaymentStatus.CANCELLED, payment.getPaymentStatus());
        verify(paymentRepository, times(1)).findBySessionId(sessionId);
        verify(notificationService, times(1))
                .notifyPaymentCancelled(payment);
        verify(paymentRepository, times(1)).save(payment);
        verifyNoMoreInteractions(paymentRepository, notificationService);
    }

    @Test
    void handlePaymentCancel_sessionIsNotExist_EntityNotFoundException() {
        String sessionId = "test";

        when(paymentRepository.findBySessionId(sessionId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            paymentService.handlePaymentCancel(sessionId);
        });

        assertEquals("Can't find payment with session id: "
                + sessionId, exception.getMessage());
        verify(paymentRepository).findBySessionId(sessionId);
        verifyNoMoreInteractions(paymentRepository);
    }

    @Test
    void getPayment_paymentIsExist_returnResponse() {
        Long paymentId = 1L;

        Payment payment = new Payment();
        payment.setId(paymentId);
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setAmountToPay(BigDecimal.valueOf(100));

        PaymentResponseDto responseDto = new PaymentResponseDto();
        responseDto.setId(payment.getId());
        responseDto.setStatus(payment.getPaymentStatus());
        responseDto.setAmountToPay(payment.getAmountToPay());

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));
        when(paymentMapper.toDto(payment)).thenReturn(responseDto);

        PaymentResponseDto actual = paymentService.getPayment(paymentId);

        assertThat(actual).isEqualTo(responseDto);
        verify(paymentRepository,times(1)).findById(paymentId);
        verify(paymentMapper,times(1)).toDto(payment);
        verifyNoMoreInteractions(paymentRepository, paymentMapper);
    }

    @Test
    void getPayment_paymentIdIsNotExist_EntityNotFoundException() {
        Long paymentId = 1L;

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            paymentService.getPayment(paymentId);
        });

        assertEquals("Can't found payment by id: " + paymentId, exception.getMessage());
        verify(paymentRepository).findById(paymentId);
        verifyNoMoreInteractions(paymentRepository);
    }

    @Test
    void calculateAmount__correctValues_Success() {
        Accommodation accommodation = new Accommodation();
        accommodation.setId(1L);
        accommodation.setLocation("Chicago");
        accommodation.setSize("6");
        accommodation.setDailyRate(new BigDecimal(123));

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setCheckInDate(LocalDate.of(2024, 8, 17));
        booking.setCheckOutDate(LocalDate.of(2024, 8, 26));
        booking.setStatus(BookingStatus.PENDING);
        booking.setUserId(1L);
        booking.setAccommodationId(1L);

        BigDecimal amount = paymentService.calculateAmount(booking, accommodation);

        BigDecimal expectedAmount = new BigDecimal("123").multiply(BigDecimal.valueOf(9));
        assertEquals(expectedAmount, amount);
    }

    @Test
    void createAndSavePayment_correctValues_Success() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setCheckInDate(LocalDate.of(2024, 8, 17));
        booking.setCheckOutDate(LocalDate.of(2024, 8, 26));
        booking.setStatus(BookingStatus.PENDING);
        booking.setUserId(1L);
        booking.setAccommodationId(1L);

        BigDecimal amountToPay = new BigDecimal("100.00");

        Payment expectedPayment = new Payment();
        expectedPayment.setBooking(booking);
        expectedPayment.setPaymentStatus(PaymentStatus.PENDING);
        expectedPayment.setAmountToPay(amountToPay);
        expectedPayment.setSessionId(SESSION_ID);
        expectedPayment.setSessionUrl(SESSION_URL);

        when(paymentRepository.save(any(Payment.class))).thenReturn(expectedPayment);

        Payment actual = paymentService.createAndSavePayment(
                booking, amountToPay, SESSION_ID, SESSION_URL);

        verify(paymentRepository).save(any(Payment.class));

        assertEquals(expectedPayment.getBooking(), actual.getBooking());
        assertEquals(expectedPayment.getPaymentStatus(), actual.getPaymentStatus());
        assertEquals(expectedPayment.getAmountToPay(), actual.getAmountToPay());
        assertEquals(expectedPayment.getSessionId(), actual.getSessionId());
        assertEquals(expectedPayment.getSessionUrl(), actual.getSessionUrl());
    }
}
