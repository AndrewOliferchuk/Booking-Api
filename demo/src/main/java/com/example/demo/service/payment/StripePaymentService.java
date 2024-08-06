package com.example.demo.service.payment;

import static com.stripe.param.checkout.SessionCreateParams.LineItem;
import static com.stripe.param.checkout.SessionCreateParams.LineItem.PriceData;
import static com.stripe.param.checkout.SessionCreateParams.LineItem.PriceData.ProductData;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripePaymentService {
    private final String apiKey;

    public StripePaymentService(@Value("${stripe.api.key}") String apiKey) {
        this.apiKey = apiKey;
    }

    @PostConstruct
    public void init() {
        Stripe.apiKey = apiKey;
    }

    public Session createStripeSession(String paymentTitle, BigDecimal amount,
            String successUrl, String cancelUrl) throws StripeException {
        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl + "?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(cancelUrl + "?session_id={CHECKOUT_SESSION_ID}")
                .addLineItem(createLineItem(paymentTitle, amount))
                .build();
        return Session.create(params);
    }

    private ProductData createProductData(String productName) {
        return SessionCreateParams.LineItem.PriceData.ProductData.builder()
                .setName(productName)
                .build();
    }

    private PriceData createPriceData(String productName, BigDecimal amount) {
        return SessionCreateParams.LineItem.PriceData.builder()
                .setCurrency("USD")
                .setUnitAmount(amount.multiply(BigDecimal.valueOf(100)).longValue())
                .setProductData(createProductData(productName))
                .build();
    }

    private LineItem createLineItem(String productName, BigDecimal amount) {
        return SessionCreateParams.LineItem.builder()
                .setQuantity(1L)
                .setPriceData(createPriceData(productName, amount))
                .build();
    }

    public String getSessionUrl(String sessionId) throws StripeException {
        Session session = Session.retrieve(sessionId);
        return session.getUrl();
    }
}
