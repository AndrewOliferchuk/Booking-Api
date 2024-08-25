package com.example.demo.controller.accommodation;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.dto.accommodation.AccommodationRequestDto;
import com.example.demo.dto.accommodation.AccommodationResponseDto;
import com.example.demo.model.enums.AccommodationType;
import com.example.demo.service.accomodation.AccommodationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.ArrayList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AccommodationControllerTest {
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private AccommodationService accommodationService;

    @BeforeAll
    static void beforeAll(
            @Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @Test
    @Sql(scripts = "classpath:database/accommodation/delete-accommodation.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("create a new accommodation")
    void createAccommodation_ValidRequestDto_Success() throws Exception {
        AccommodationRequestDto requestDto = new AccommodationRequestDto();
        requestDto.setSize("10");
        requestDto.setLocation("Rivne");
        requestDto.setDailyRate(new BigDecimal(160));
        requestDto.setType(AccommodationType.CONDO);
        requestDto.setAvailability(123);
        requestDto.setAmenities(new ArrayList<>());

        AccommodationResponseDto responseDto = new AccommodationResponseDto();
        responseDto.setId(1L);
        responseDto.setSize(requestDto.getSize());
        responseDto.setLocation(requestDto.getLocation());
        responseDto.setDailyRate(requestDto.getDailyRate());
        responseDto.setType(requestDto.getType());
        responseDto.setAvailability(requestDto.getAvailability());
        responseDto.setAmenities(responseDto.getAmenities());

        given(accommodationService.create(requestDto)).willReturn(responseDto);

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(post("/accommodations")
                .content(jsonRequest)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        AccommodationResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), AccommodationResponseDto.class);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(actual.getLocation(), responseDto.getLocation());
        Assertions.assertEquals(actual.getSize(), responseDto.getSize());
        Assertions.assertEquals(actual.getDailyRate(), responseDto.getDailyRate());
        Assertions.assertEquals(actual.getType(), responseDto.getType());
        Assertions.assertEquals(actual.getAmenities(), responseDto.getAmenities());
        Assertions.assertEquals(actual.getAvailability(), responseDto.getAvailability());
    }

    @WithMockUser(username = "customer", roles = {"CUSTOMER", "MANAGER"})
    @Sql(scripts = "classpath:database/accommodation/delete-accommodation.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getById_ValidAccommodationId_Success() throws Exception {
        Long accommodationId = 1L;

        AccommodationResponseDto responseDto = new AccommodationResponseDto();
        responseDto.setId(accommodationId);
        responseDto.setSize("10");
        responseDto.setLocation("Rivne");
        responseDto.setDailyRate(new BigDecimal(160));
        responseDto.setType(AccommodationType.CONDO);
        responseDto.setAvailability(123);
        responseDto.setAmenities(new ArrayList<>());

        when(accommodationService.getById(accommodationId)).thenReturn(responseDto);

        MvcResult result = mockMvc.perform(get("/accommodations/{id}", accommodationId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        AccommodationResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), AccommodationResponseDto.class);

        Assertions.assertEquals(actual.getLocation(), responseDto.getLocation());
        Assertions.assertEquals(actual.getSize(), responseDto.getSize());
        Assertions.assertEquals(actual.getDailyRate(), responseDto.getDailyRate());
        Assertions.assertEquals(actual.getType(), responseDto.getType());
        Assertions.assertEquals(actual.getAmenities(), responseDto.getAmenities());
        Assertions.assertEquals(actual.getAvailability(), responseDto.getAvailability());
    }

    @WithMockUser(username = "customer", roles = {"CUSTOMER", "MANAGER"})
    @DisplayName("delete accommodation by id")
    @Test
    void deleteAccommodation_ValidAccommodationId_Success() throws Exception {
        Long accommodationId = 1L;

        Mockito.doNothing().when(accommodationService).deleteById(accommodationId);

        mockMvc.perform(delete("/accommodations/{id}", accommodationId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        Mockito.verify(accommodationService, Mockito.times(
                1)).deleteById(accommodationId);
    }

    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @Test
    @Sql(scripts = "classpath:database/accommodation/delete-accommodation.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Update accommodation by ID")
    void updateAccommodation_ValidIdAndRequestDto_Success() throws Exception {
        AccommodationRequestDto requestDto = new AccommodationRequestDto();
        requestDto.setSize("10");
        requestDto.setLocation("Rivne");
        requestDto.setDailyRate(new BigDecimal(160));
        requestDto.setType(AccommodationType.CONDO);
        requestDto.setAvailability(123);
        requestDto.setAmenities(new ArrayList<>());

        Long accommodationId = 1L;

        AccommodationResponseDto responseDto = new AccommodationResponseDto();
        responseDto.setId(accommodationId);
        responseDto.setSize(requestDto.getSize());
        responseDto.setLocation(requestDto.getLocation());
        responseDto.setDailyRate(requestDto.getDailyRate());
        responseDto.setType(requestDto.getType());
        responseDto.setAvailability(requestDto.getAvailability());
        responseDto.setAmenities(responseDto.getAmenities());

        Mockito.when(accommodationService.updateById(
                accommodationId, requestDto)).thenReturn(responseDto);

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(put("/accommodations/{id}", accommodationId)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        AccommodationResponseDto actual = objectMapper.readValue(
                jsonResponse, AccommodationResponseDto.class);

        Assertions.assertEquals(responseDto.getId(), actual.getId());
        Assertions.assertEquals(responseDto.getSize(), actual.getSize());
        Assertions.assertEquals(responseDto.getLocation(), actual.getLocation());
        Assertions.assertEquals(responseDto.getDailyRate(), actual.getDailyRate());
        Assertions.assertEquals(responseDto.getType(), actual.getType());
        Assertions.assertEquals(responseDto.getAvailability(), actual.getAvailability());
        Assertions.assertEquals(responseDto.getAmenities(), actual.getAmenities());
    }
}
