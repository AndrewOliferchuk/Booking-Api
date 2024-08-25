package com.example.demo.service.notification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.model.Accommodation;
import com.example.demo.model.Booking;
import com.example.demo.model.Payment;
import com.example.demo.model.enums.BookingStatus;
import com.example.demo.model.enums.PaymentStatus;
import com.example.demo.repository.BookingRepository;
import com.example.demo.service.telegram.TelegramBot;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {
    public static final long CHAT_ID_1 = 123456789L;
    public static final long CHAT_ID_2 = 987654321L;

    @Mock
    private TelegramBot telegramBot;
    @Mock
    private BookingRepository bookingRepository;
    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Test
    void notifyNewBooking_notifyBooking_sendsNotification() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setCheckInDate(LocalDate.of(2024, 8, 17));
        booking.setCheckOutDate(LocalDate.of(2024, 8, 26));
        booking.setStatus(BookingStatus.PENDING);
        booking.setUserId(1L);
        booking.setAccommodationId(1L);

        notificationService.subscribe(CHAT_ID_1);
        notificationService.subscribe(CHAT_ID_2);

        notificationService.notifyNewBooking(booking);

        String expected = String.format("New booking created:\n%s", booking.toString());
        verify(telegramBot).sendMessage(CHAT_ID_1, expected);
        verify(telegramBot).sendMessage(CHAT_ID_2, expected);
    }

    @Test
    void notifyBookingCanceled_notifyCanceledBooking_sendsNotification() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setCheckInDate(LocalDate.of(2024, 8, 17));
        booking.setCheckOutDate(LocalDate.of(2024, 8, 26));
        booking.setStatus(BookingStatus.PENDING);
        booking.setUserId(1L);
        booking.setAccommodationId(1L);

        notificationService.subscribe(CHAT_ID_1);
        notificationService.subscribe(CHAT_ID_2);

        notificationService.notifyBookingCanceled(booking);

        String expected = String.format("Booking canceled:\n%s", booking.toString());
        verify(telegramBot).sendMessage(CHAT_ID_1, expected);
        verify(telegramBot).sendMessage(CHAT_ID_2, expected);
    }

    @Test
    void notifyNewAccommodation_notifyNewAccommodation_sendsNotification() {
        Accommodation accommodation = new Accommodation();
        accommodation.setId(1L);
        accommodation.setLocation("Chicago");
        accommodation.setSize("6");
        accommodation.setDailyRate(new BigDecimal(123));

        notificationService.subscribe(CHAT_ID_1);
        notificationService.subscribe(CHAT_ID_2);

        notificationService.notifyNewAccommodation(accommodation);

        String expected = String.format("New accommodation created:\n%s",
                accommodation.toString());
        verify(telegramBot).sendMessage(CHAT_ID_1, expected);
        verify(telegramBot).sendMessage(CHAT_ID_2, expected);
    }

    @Test
    void notifyAccommodationReleased_notifyAccommodation_sendsNotification() {
        Accommodation accommodation = new Accommodation();
        accommodation.setId(1L);
        accommodation.setLocation("Chicago");
        accommodation.setSize("6");
        accommodation.setDailyRate(new BigDecimal(123));

        notificationService.subscribe(CHAT_ID_1);
        notificationService.subscribe(CHAT_ID_2);

        notificationService.notifyAccommodationReleased(accommodation);

        String expected = String.format("Accommodation released:\n%s", accommodation.toString());
        verify(telegramBot).sendMessage(CHAT_ID_2, expected);
        verify(telegramBot).sendMessage(CHAT_ID_2, expected);
    }

    @Test
    void notifyPaymentSuccess_shouldNotifyPayment_Success() {
        Payment payment = new Payment();
        payment.setId(1L);
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setAmountToPay(BigDecimal.valueOf(100));

        notificationService.subscribe(CHAT_ID_1);
        notificationService.subscribe(CHAT_ID_2);

        notificationService.notifyPaymentSuccess(payment);

        String expected = String.format("Payment successful:\n%s", payment.toString());
        verify(telegramBot).sendMessage(CHAT_ID_1, expected);
        verify(telegramBot).sendMessage(CHAT_ID_2, expected);
    }

    @Test
    void notifyPaymentCancelled_shouldNotifyPayment_Success() {
        Payment payment = new Payment();
        payment.setId(1L);
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setAmountToPay(BigDecimal.valueOf(100));

        notificationService.subscribe(CHAT_ID_1);
        notificationService.subscribe(CHAT_ID_2);

        notificationService.notifyPaymentCancelled(payment);

        String expected = String.format("Payment cancelled:\n%s", payment.toString());
        verify(telegramBot).sendMessage(CHAT_ID_1, expected);
        verify(telegramBot).sendMessage(CHAT_ID_2, expected);
    }

    @Test
    void notifyExpiredBookings_ExpiredBookingsExist_UpdatesStatusAndSendsNotifications() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setCheckInDate(LocalDate.of(2024, 8, 17));
        booking.setCheckOutDate(LocalDate.of(2024, 8, 26));
        booking.setStatus(BookingStatus.PENDING);
        booking.setUserId(1L);
        booking.setAccommodationId(1L);

        Booking booking2 = new Booking();
        booking2.setId(2L);
        booking2.setCheckInDate(LocalDate.of(2024, 8, 15));
        booking2.setCheckOutDate(LocalDate.of(2024, 8, 16));
        booking2.setStatus(BookingStatus.PENDING);
        booking2.setUserId(1L);
        booking2.setAccommodationId(1L);

        List<Booking> result = List.of(booking, booking2);

        LocalDate tomorrow = LocalDate.now().plusDays(1);

        when(bookingRepository.findExpiredBookings(tomorrow)).thenReturn(result);

        notificationService.notifyExpiredBookings();

        assertEquals(BookingStatus.EXPIRED, booking.getStatus());
        assertEquals(BookingStatus.EXPIRED, booking2.getStatus());
        verify(bookingRepository).findExpiredBookings(tomorrow);
    }
}
