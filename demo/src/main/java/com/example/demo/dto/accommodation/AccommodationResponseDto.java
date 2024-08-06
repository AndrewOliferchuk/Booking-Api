package com.example.demo.dto.accommodation;

import com.example.demo.model.enums.AccommodationType;
import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

@Data
public class AccommodationResponseDto {
    private Long id;
    private AccommodationType type;
    private String location;
    private String size;
    private List<String> amenities;
    private BigDecimal dailyRate;
    private Integer availability;
}
