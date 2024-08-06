package com.example.demo.service.accomodation;

import com.example.demo.dto.accommodation.AccommodationRequestDto;
import com.example.demo.dto.accommodation.AccommodationResponseDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface AccommodationService {
    AccommodationResponseDto create(AccommodationRequestDto requestDto);

    List<AccommodationResponseDto> findAll(Pageable pageable);

    AccommodationResponseDto getById(Long id);

    AccommodationResponseDto updateById(Long id, AccommodationRequestDto requestDto);

    void deleteById(Long id);
}
