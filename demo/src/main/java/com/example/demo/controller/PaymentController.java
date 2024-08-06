package com.example.demo.controller;

import com.example.demo.dto.payment.PaymentResponseDto;
import com.example.demo.model.User;
import com.example.demo.service.payment.PaymentService;
import com.stripe.exception.StripeException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Payment management",
        description = "Endpoints for managing payments via Stripe API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Create payment session",
            description = "Initiates payment sessions for booking transactions.")
    public String createPaymentSession(@RequestParam Long bookingId) throws StripeException {
        return paymentService.createPayment(bookingId);
    }

    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    @ResponseStatus(HttpStatus.OK)
    public List<PaymentResponseDto> getAll(@AuthenticationPrincipal User user) {
        return paymentService.getPaymentsByUserId(user.getId());
    }

    @GetMapping("/success")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Payment success",
            description = "Handles successful payment processing through Stripe redirection.")
    public String paymentSuccess(@RequestParam String sessionId) {
        paymentService.handlePaymentSuccess(sessionId);
        return "Payment successful!";
    }

    @GetMapping("/cancel")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Payment cancellation",
                description = "Manages payment cancellation and returns "
            + "payment paused messages during Stripe redirection.")
    public String paymentCancel(@RequestParam String sessionId) {
        paymentService.handlePaymentCancel(sessionId);
        return "Payment cancelled";
    }
}
