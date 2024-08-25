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
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StripePaymentService {
    private static final String USD = "usd";
    private static final int HUNDRED = 100;

    @Value("${stripe.api.key}")
    private String apiKey;

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

    public String getSessionUrl(String sessionId) throws StripeException {
        Session session = Session.retrieve(sessionId);
        return session.getUrl();
    }

    private ProductData createProductData(String productName) {
        return SessionCreateParams.LineItem.PriceData.ProductData.builder()
                .setName(productName)
                .build();
    }

    private PriceData createPriceData(String productName, BigDecimal amount) {
        return SessionCreateParams.LineItem.PriceData.builder()
                .setCurrency(USD)
                .setUnitAmount(amount.multiply(BigDecimal.valueOf(HUNDRED)).longValue())
                .setProductData(createProductData(productName))
                .build();
    }

    private LineItem createLineItem(String productName, BigDecimal amount) {
        return SessionCreateParams.LineItem.builder()
                .setQuantity(1L)
                .setPriceData(createPriceData(productName, amount))
                .build();
    }
}
