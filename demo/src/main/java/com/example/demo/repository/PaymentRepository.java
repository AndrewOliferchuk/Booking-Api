package com.example.demo.repository;

import com.example.demo.model.Payment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findAllByBookingUserId(Long id);

    Optional<Payment> findBySessionId(String sessionId);
}
