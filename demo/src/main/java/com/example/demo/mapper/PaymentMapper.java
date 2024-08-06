package com.example.demo.mapper;

import com.example.demo.config.MapperConfig;
import com.example.demo.dto.payment.PaymentResponseDto;
import com.example.demo.model.Payment;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface PaymentMapper {
    PaymentResponseDto toDto(Payment payment);
}
