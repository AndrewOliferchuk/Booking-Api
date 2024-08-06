package com.example.demo.service.notification;

import com.example.demo.model.Accommodation;
import com.example.demo.model.Booking;
import com.example.demo.model.Payment;

public interface NotificationService {
    boolean subscribe(long chatId);

    boolean unsubscribe(long chatId);

    void sendNotification(String message);

    void notifyNewBooking(Booking booking);

    void notifyBookingCanceled(Booking booking);

    void notifyNewAccommodation(Accommodation accommodation);

    void notifyAccommodationReleased(Accommodation accommodation);

    void notifyPaymentSuccess(Payment payment);

    void notifyPaymentCancelled(Payment payment);

    void notifyExpiredBookings();
}
