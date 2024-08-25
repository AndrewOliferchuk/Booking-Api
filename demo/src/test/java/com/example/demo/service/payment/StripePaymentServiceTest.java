package com.example.demo.service.payment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StripePaymentServiceTest {
    public static final String PAYMENT_TITLE = "Test Payment";
    public static final BigDecimal AMOUNT = new BigDecimal("100.00");
    public static final String SUCCESS_URL = "https://example.com/success";
    public static final String CANCEL_URL = "https://example.com/cancel";
    public static final String SESSION_URL = "https://example.com/session_url";
    public static final String SESSION_ID = "session_id";

    @InjectMocks
    private StripePaymentService stripePaymentService;
    @Mock
    private Session mockSession;

    @Test
    void createStripeSession() throws StripeException {
        when(mockSession.getId()).thenReturn(SESSION_ID);
        when(mockSession.getUrl()).thenReturn(SESSION_URL);
        mockStatic(Session.class);
        when(Session.create(any(SessionCreateParams.class))).thenReturn(mockSession);

        Session result = stripePaymentService.createStripeSession(
                PAYMENT_TITLE, AMOUNT, SUCCESS_URL, CANCEL_URL);

        assertEquals(SESSION_ID, result.getId());
        assertEquals(SESSION_URL, result.getUrl());

        verify(Session.class, times(1));
        Session.create(any(SessionCreateParams.class));
    }
}
