package com.example.demo.mapper;

import com.example.demo.config.MapperConfig;
import com.example.demo.dto.booking.BookingRequestDto;
import com.example.demo.dto.booking.BookingResponseDto;
import com.example.demo.model.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface BookingMapper {
    Booking toModel(BookingRequestDto requestDto);

    BookingResponseDto toDto(Booking booking);

    void updateBooking(BookingRequestDto requestDto,
                             @MappingTarget Booking entity);
}
