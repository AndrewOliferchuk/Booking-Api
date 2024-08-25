package com.example.demo.controller;

import com.example.demo.dto.accommodation.AccommodationRequestDto;
import com.example.demo.dto.accommodation.AccommodationResponseDto;
import com.example.demo.service.accomodation.AccommodationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Accommodation management",
        description = "Endpoints for managing accommodations")
@RequiredArgsConstructor
@RestController
@RequestMapping("/accommodations")
public class AccommodationController {

    private final AccommodationService accommodationService;

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new accommodation",
            description = "Create a new accommodation")
    public AccommodationResponseDto createAccommodation(
            @RequestBody @Valid AccommodationRequestDto requestDto) {
        return accommodationService.create(requestDto);
    }

    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Get all accommodations",
            description = "Get a list of all available accommodations")
    public List<AccommodationResponseDto> getAll(Pageable pageable) {
        return accommodationService.findAll(pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Get accommodation by id",
            description = "Get accommodation by unique id")
    public AccommodationResponseDto getById(@PathVariable Long id) {
        return accommodationService.getById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Update accommodation",
            description = "Update accommodation by id")
    public AccommodationResponseDto updateAccommodation(@PathVariable @Positive Long id,
                          @RequestBody @Valid AccommodationRequestDto requestDto) {
        return accommodationService.updateById(id, requestDto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('MANAGER')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete accommodation", description = "Delete accommodation by id")
    public void deleteAccommodation(@PathVariable Long id) {
        accommodationService.deleteById(id);
    }
}
