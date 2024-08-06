package com.example.demo.service.payment;

import static java.time.temporal.ChronoUnit.DAYS;

import com.example.demo.dto.payment.PaymentResponseDto;
import com.example.demo.exception.EntityNotFoundException;
import com.example.demo.mapper.PaymentMapper;
import com.example.demo.model.Accommodation;
import com.example.demo.model.Booking;
import com.example.demo.model.Payment;
import com.example.demo.model.enums.PaymentStatus;
import com.example.demo.repository.AccommodationRepository;
import com.example.demo.repository.BookingRepository;
import com.example.demo.repository.PaymentRepository;
import com.example.demo.service.notification.NotificationService;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private static final String BOOKING = "BOOKING";
    private static final String FROM = "FROM";
    private static final String TO = "TO";
    private static final String SPACE = " ";
    private static final String PATH_SUCCESS = "/payments/success/";
    private static final String PATH_CANCEL = "/payments/cancel/";

    @Value("${app.base.url}")
    private String baseUrl;
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final StripePaymentService stripePaymentService;
    private final BookingRepository bookingRepository;
    private final AccommodationRepository accommodationRepository;
    private final NotificationService notificationService;

    @Override
    public String createPayment(Long bookingId) throws StripeException {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new EntityNotFoundException("Can't found booking by id: " + bookingId)
        );

        Accommodation accommodation = accommodationRepository.findById(
                booking.getAccommodationId()).orElseThrow(
                        () -> new EntityNotFoundException("Can't found accommodation by id: "
                                + booking.getAccommodationId()));

        BigDecimal amountToPay = calculateAmount(booking, accommodation);
        String paymentTitle = getBookingTitle(booking, accommodation);
        String successUrl = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path(PATH_SUCCESS).toUriString();
        String cancelUrl = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path(PATH_CANCEL).toUriString();
        Session session = stripePaymentService.createStripeSession(paymentTitle,
                amountToPay, successUrl, cancelUrl);

        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setAmountToPay(amountToPay);
        payment.setSessionId(session.getId());
        payment.setSessionUrl(session.getUrl());
        paymentRepository.save(payment);
        return session.getUrl();
    }

    private BigDecimal calculateAmount(Booking booking, Accommodation accommodation) {
        BigDecimal dailyRate = accommodation.getDailyRate();
        long days = DAYS.between(booking.getCheckInDate(), booking.getCheckOutDate());
        return dailyRate.multiply(BigDecimal.valueOf(days));
    }

    private static String getBookingTitle(Booking booking, Accommodation accommodation) {
        return BOOKING + SPACE
                + accommodation.getLocation() + SPACE
                + FROM + SPACE + booking.getCheckInDate().toString() + SPACE
                + TO + SPACE + booking.getCheckOutDate().toString();
    }

    @Override
    public List<PaymentResponseDto> getPaymentsByUserId(Long userId) {
        return paymentRepository.findAllByBookingUserId(userId).stream()
                .map(paymentMapper::toDto)
                .toList();
    }

    @Override
    public PaymentResponseDto getPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId).orElseThrow(
                () -> new EntityNotFoundException("Can't found payment by id: " + paymentId)
        );
        return paymentMapper.toDto(payment);
    }

    @Override
    public void handlePaymentSuccess(String sessionId) {
        Payment payment = paymentRepository.findBySessionId(sessionId).orElseThrow(
                (() -> new EntityNotFoundException("Can't find payment with session id: "
                        + sessionId))
        );
        payment.setPaymentStatus(PaymentStatus.PAID);
        notificationService.notifyPaymentSuccess(payment);
        paymentRepository.save(payment);
    }

    @Override
    public void handlePaymentCancel(String sessionId) {
        Payment payment = paymentRepository.findBySessionId(sessionId).orElseThrow(
                () -> new EntityNotFoundException("Can't find payment with session id: "
                        + sessionId));
        payment.setPaymentStatus(PaymentStatus.CANCELLED);
        notificationService.notifyPaymentCancelled(payment);
        paymentRepository.save(payment);
    }
}
