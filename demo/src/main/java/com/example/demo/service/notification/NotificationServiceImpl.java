package com.example.demo.service.notification;

import com.example.demo.model.Accommodation;
import com.example.demo.model.Booking;
import com.example.demo.model.Payment;
import com.example.demo.model.enums.BookingStatus;
import com.example.demo.repository.AccommodationRepository;
import com.example.demo.repository.BookingRepository;
import com.example.demo.repository.PaymentRepository;
import com.example.demo.service.telegram.TelegramBot;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final Set<Long> subscribedUsers = new HashSet<>();
    private final TelegramBot telegramBot;
    private final BookingRepository bookingRepository;
    private final AccommodationRepository accommodationRepository;
    private final PaymentRepository paymentRepository;

    @Override
    public boolean subscribe(long chatId) {
        return subscribedUsers.add(chatId);
    }

    @Override
    public boolean unsubscribe(long chatId) {
        return subscribedUsers.add(chatId);
    }

    @Override
    public void sendNotification(String message) {
        for (Long chatId : subscribedUsers) {
            telegramBot.sendMessage(chatId, message);
        }
    }

    @Override
    public void notifyNewBooking(Booking booking) {
        String message = String.format("New booking created:\n%s", booking.toString());
        sendNotification(message);
    }

    @Override
    public void notifyBookingCanceled(Booking booking) {
        String message = String.format("Booking canceled:\n%s", booking.toString());
        sendNotification(message);
    }

    @Override
    public void notifyNewAccommodation(Accommodation accommodation) {
        String message = String.format("New accommodation created:\n%s", accommodation.toString());
        sendNotification(message);
    }

    @Override
    public void notifyAccommodationReleased(Accommodation accommodation) {
        String message = String.format("Accommodation released:\n%s", accommodation.toString());
        sendNotification(message);
    }

    @Override
    public void notifyPaymentSuccess(Payment payment) {
        String message = String.format("Payment successful:\n%s", payment.toString());
        sendNotification(message);
    }

    @Override
    public void notifyPaymentCancelled(Payment payment) {
        String message = String.format("Payment cancelled:\n%s", payment.toString());
        sendNotification(message);
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void notifyExpiredBookings() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        List<Booking> expiredBookings = bookingRepository.findExpiredBookings(tomorrow);
        if (expiredBookings.isEmpty()) {
            sendNotification("No expired bookings today!");
        } else {
            for (Booking booking : expiredBookings) {
                booking.setStatus(BookingStatus.EXPIRED);
                bookingRepository.save(booking);
                sendNotification("Booking expired: " + booking.toString());
            }
        }
    }
}
