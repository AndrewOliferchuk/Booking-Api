package com.example.demo.dto.accommodation;

import com.example.demo.model.enums.AccommodationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

@Data
public class AccommodationRequestDto {
    private AccommodationType type;
    @NotBlank
    private String location;
    @NotBlank
    private String size;
    private List<String> amenities;
    @Positive
    private BigDecimal dailyRate;
    private Integer availability;
}
