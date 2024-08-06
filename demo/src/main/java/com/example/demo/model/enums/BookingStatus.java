package com.example.demo.model.enums;

public enum BookingStatus {
    PENDING,
    CONFIRMED,
    CANCELED,
    EXPIRED;

    public static BookingStatus getString(String string) {
        for (BookingStatus status : BookingStatus.values()) {
            if (status.name().equalsIgnoreCase(string)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid status: " + string);
    }
}
