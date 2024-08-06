package com.example.demo.service.payment;

import com.example.demo.dto.payment.PaymentResponseDto;
import com.stripe.exception.StripeException;
import java.util.List;

public interface PaymentService {
    String createPayment(Long bookingId) throws StripeException;

    List<PaymentResponseDto> getPaymentsByUserId(Long userId);

    PaymentResponseDto getPayment(Long paymentId);

    void handlePaymentSuccess(String sessionId);

    void handlePaymentCancel(String sessionId);
}
