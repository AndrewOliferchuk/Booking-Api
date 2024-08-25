package com.example.demo.mapper;

import com.example.demo.config.MapperConfig;
import com.example.demo.dto.accommodation.AccommodationRequestDto;
import com.example.demo.dto.accommodation.AccommodationResponseDto;
import com.example.demo.model.Accommodation;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface AccommodationMapper {

    AccommodationResponseDto toDto(Accommodation accommodation);

    Accommodation toModel(AccommodationRequestDto accommodationRequestDto);

    void updateAccommodation(AccommodationRequestDto requestDto,
                             @MappingTarget Accommodation entity);
}
