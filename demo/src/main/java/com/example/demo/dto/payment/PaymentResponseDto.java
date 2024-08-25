package com.example.demo.dto.payment;

import com.example.demo.model.enums.PaymentStatus;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class PaymentResponseDto {
    private Long id;
    private PaymentStatus status;
    private Long bookingId;
    private String sessionId;
    private String sessionUrl;
    private BigDecimal amountToPay;
}
