package com.example.demo.service.accomodation;

import com.example.demo.dto.accommodation.AccommodationRequestDto;
import com.example.demo.dto.accommodation.AccommodationResponseDto;
import com.example.demo.exception.EntityNotFoundException;
import com.example.demo.mapper.AccommodationMapper;
import com.example.demo.model.Accommodation;
import com.example.demo.repository.AccommodationRepository;
import com.example.demo.service.notification.NotificationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AccommodationServiceImpl implements AccommodationService {
    private final AccommodationRepository accommodationRepository;
    private final AccommodationMapper accommodationMapper;
    private final NotificationService notificationService;

    @Override
    public AccommodationResponseDto create(AccommodationRequestDto requestDto) {
        Accommodation accommodation = accommodationMapper.toModel(requestDto);
        notificationService.notifyNewAccommodation(accommodation);
        return accommodationMapper.toDto(accommodationRepository.save(accommodation));
    }

    @Override
    public List<AccommodationResponseDto> findAll(Pageable pageable) {
        return accommodationRepository.findAll(pageable)
                .stream()
                .map(accommodationMapper::toDto)
                .toList();
    }

    @Override
    public AccommodationResponseDto getById(Long id) {
        Accommodation accommodation = accommodationRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't get accommodation by id: " + id));
        return accommodationMapper.toDto(accommodation);
    }

    @Override
    public AccommodationResponseDto updateById(Long id, AccommodationRequestDto requestDto) {
        Accommodation accommodation = accommodationRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Accommodation not found by id: " + id)
        );
        accommodationMapper.updateAccommodation(requestDto, accommodation);
        return accommodationMapper.toDto(accommodationRepository.save(accommodation));
    }

    @Override
    public void deleteById(Long id) {
        Accommodation accommodation = accommodationRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Accommodation not found by id: " + id)
        );
        accommodationRepository.deleteById(id);
        notificationService.notifyAccommodationReleased(accommodation);
    }
}
