package com.example.demo.repository;

import com.example.demo.model.Booking;
import com.example.demo.model.enums.BookingStatus;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserIdAndStatus(Long userId, BookingStatus status);

    List<Booking> findBookingsByUserId(Long userId);

    @Query("SELECT b FROM Booking b WHERE b.checkOutDate <= :tomorrow AND b.status != 'CANCELLED'")
    List<Booking> findExpiredBookings(LocalDate tomorrow);
}
