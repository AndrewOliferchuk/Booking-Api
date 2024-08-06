package com.example.demo.model;

import com.example.demo.model.enums.BookingStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@Setter
@SQLDelete(sql = "UPDATE bookings SET is_deleted = TRUE WHERE id=?")
@SQLRestriction(value = "is_deleted=FALSE")
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "check_in_date", nullable = false)
    private LocalDate checkInDate;
    @Column(name = "check_out_date", nullable = false)
    private LocalDate checkOutDate;
    @Column(name = "accommodation_id", nullable = false)
    private Long accommodationId;
    @Column(name = "user_id", nullable = false)
    private Long userId;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;
    @Column(nullable = false)
    private boolean isDeleted = false;
}
