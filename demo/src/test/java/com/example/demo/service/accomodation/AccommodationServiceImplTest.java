package com.example.demo.service.accomodation;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.example.demo.dto.accommodation.AccommodationRequestDto;
import com.example.demo.dto.accommodation.AccommodationResponseDto;
import com.example.demo.exception.EntityNotFoundException;
import com.example.demo.mapper.AccommodationMapper;
import com.example.demo.model.Accommodation;
import com.example.demo.repository.AccommodationRepository;
import com.example.demo.service.notification.NotificationService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class AccommodationServiceImplTest {
    @InjectMocks
    private AccommodationServiceImpl accommodationService;
    @Mock
    private AccommodationRepository accommodationRepository;
    @Mock
    private AccommodationMapper accommodationMapper;
    @Mock
    private NotificationService notificationService;

    @Test
    @DisplayName("Verify create, method works")
    void create_ValidCreateRequestDto_returnResponseDto() {
        AccommodationRequestDto requestDto = new AccommodationRequestDto();
        requestDto.setSize("10");
        requestDto.setLocation("Rivne");
        requestDto.setDailyRate(new BigDecimal(160));

        Accommodation accommodation = new Accommodation();
        accommodation.setLocation(requestDto.getLocation());
        accommodation.setSize(requestDto.getSize());
        accommodation.setDailyRate(requestDto.getDailyRate());

        AccommodationResponseDto responseDto = new AccommodationResponseDto();
        responseDto.setId(1L);
        responseDto.setSize(accommodation.getSize());
        responseDto.setLocation(accommodation.getLocation());
        responseDto.setDailyRate(accommodation.getDailyRate());

        when(accommodationMapper.toModel(requestDto)).thenReturn(accommodation);
        when(accommodationRepository.save(accommodation)).thenReturn(accommodation);
        when(accommodationMapper.toDto(accommodation)).thenReturn(responseDto);

        AccommodationResponseDto createResponse = accommodationService.create(requestDto);

        assertThat(createResponse).isEqualTo(responseDto);
        verify(accommodationMapper, times(1)).toModel(requestDto);
        verify(notificationService, times(1)).notifyNewAccommodation(accommodation);
        verify(accommodationRepository, times(1)).save(accommodation);
        verify(accommodationMapper, times(1)).toDto(accommodation);
        verifyNoMoreInteractions(accommodationRepository, notificationService, accommodationMapper);
    }

    @Test
    @DisplayName("Verify findAll, method works")
    void findAll_ValidPageable_returnAllBooking() {
        Accommodation accommodation = new Accommodation();
        accommodation.setId(1L);
        accommodation.setLocation("Chicago");
        accommodation.setSize("6");
        accommodation.setDailyRate(new BigDecimal(123));

        AccommodationResponseDto responseDto = new AccommodationResponseDto();
        responseDto.setId(accommodation.getId());
        responseDto.setLocation(accommodation.getLocation());
        responseDto.setSize(accommodation.getSize());
        responseDto.setDailyRate(accommodation.getDailyRate());

        Pageable pageable = PageRequest.of(0, 10);
        List<Accommodation> accommodationList = List.of(accommodation);
        Page<Accommodation> accommodationPage = new PageImpl<>(accommodationList, pageable,
                accommodationList.size());

        when(accommodationRepository.findAll(pageable)).thenReturn(accommodationPage);
        when(accommodationMapper.toDto(accommodation)).thenReturn(responseDto);

        List<AccommodationResponseDto> response = accommodationService.findAll(pageable);

        assertThat(response.get(0)).isEqualTo(responseDto);

        verify(accommodationRepository, times(1)).findAll(pageable);
        verify(accommodationMapper, times(1)).toDto(accommodation);
        verifyNoMoreInteractions(accommodationRepository, accommodationMapper);
    }

    @Test
    void getById_ValidFoundById_returnResponseDto() {
        Accommodation accommodation = new Accommodation();
        accommodation.setId(1L);
        accommodation.setLocation("New York");
        accommodation.setSize("8");
        accommodation.setDailyRate(new BigDecimal(310));

        AccommodationResponseDto responseDto = new AccommodationResponseDto();
        responseDto.setId(accommodation.getId());
        responseDto.setLocation(accommodation.getLocation());
        responseDto.setSize(accommodation.getSize());
        responseDto.setDailyRate(accommodation.getDailyRate());

        when(accommodationMapper.toDto(accommodation)).thenReturn(responseDto);
        when(accommodationRepository.findById(responseDto.getId()))
                .thenReturn(Optional.of(accommodation));

        AccommodationResponseDto result = accommodationService.getById(responseDto.getId());

        assertThat(result).isEqualTo(responseDto);
        verify(accommodationMapper, times(1)).toDto(accommodation);
        verify(accommodationRepository, times(1))
                .findById(responseDto.getId());
        verifyNoMoreInteractions(accommodationRepository, accommodationMapper);
    }

    @Test
    @DisplayName("Update booking by ID - success")
    void updateById_validUpdateByIdAndRequest_returnUpdateResponse() {
        Long accommodationId = 1L;

        Accommodation accommodation = new Accommodation();
        accommodation.setId(accommodationId);
        accommodation.setLocation("New York");
        accommodation.setSize("8");
        accommodation.setDailyRate(new BigDecimal(310));

        AccommodationRequestDto requestDto = new AccommodationRequestDto();
        requestDto.setSize("10");
        requestDto.setLocation("Rivne");
        requestDto.setDailyRate(new BigDecimal(160));

        AccommodationResponseDto responseDto = new AccommodationResponseDto();
        responseDto.setId(accommodationId);
        responseDto.setLocation(accommodation.getLocation());
        responseDto.setSize(accommodation.getSize());
        responseDto.setDailyRate(accommodation.getDailyRate());

        when(accommodationRepository.findById(accommodationId)).thenReturn(
                Optional.of(accommodation));
        doNothing().when(accommodationMapper).updateAccommodation(requestDto, accommodation);
        when(accommodationRepository.save(accommodation)).thenReturn(accommodation);
        when(accommodationMapper.toDto(accommodation)).thenReturn(responseDto);

        AccommodationResponseDto result = accommodationService.updateById(
                accommodationId, requestDto);

        assertThat(result).isEqualTo(responseDto);
        verify(accommodationRepository).findById(accommodationId);
        verify(accommodationRepository).save(accommodation);
        verify(accommodationMapper).updateAccommodation(requestDto, accommodation);
        verify(accommodationMapper).toDto(accommodation);
        verifyNoMoreInteractions(accommodationRepository, accommodationMapper);
    }

    @Test
    void updateById_InvalidId_ThrowsEntityNotFoundException() {
        Long accommodationId = 1L;

        AccommodationRequestDto requestDto = new AccommodationRequestDto();

        when(accommodationRepository.findById(accommodationId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            accommodationService.updateById(accommodationId, requestDto);
        });

        assertEquals("Accommodation not found by id: " + accommodationId,
                exception.getMessage());
        verify(accommodationRepository).findById(accommodationId);
        verifyNoMoreInteractions(accommodationRepository);
    }

    @Test
    void deleteById_mustDelete_WhenIdExist() {
        Long accommodationId = 1L;

        Accommodation accommodation = new Accommodation();
        accommodation.setId(accommodationId);
        accommodation.setLocation("New York");
        accommodation.setSize("8");
        accommodation.setDailyRate(new BigDecimal(310));

        when(accommodationRepository.findById(accommodationId)).thenReturn(
                Optional.of(accommodation));

        accommodationService.deleteById(accommodationId);
        verify(accommodationRepository, times(1)).deleteById(accommodationId);
        verify(notificationService, times(1))
                .notifyAccommodationReleased(accommodation);
    }

    @Test
    void deleteById_EmptyValue_returnEntityNotFoundException() {
        Long accommodationId = 1L;

        when(accommodationRepository.findById(accommodationId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            accommodationService.deleteById(accommodationId);
        });

        assertEquals("Accommodation not found by id: " + accommodationId,
                exception.getMessage());
        verify(accommodationRepository).findById(accommodationId);
        verifyNoMoreInteractions(accommodationRepository);
    }
}
