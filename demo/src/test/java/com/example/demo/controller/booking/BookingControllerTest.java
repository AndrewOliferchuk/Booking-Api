package com.example.demo.controller.booking;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.dto.booking.BookingRequestDto;
import com.example.demo.dto.booking.BookingResponseDto;
import com.example.demo.model.enums.BookingStatus;
import com.example.demo.service.booking.BookingService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.List;
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
class BookingControllerTest {
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private BookingService bookingService;

    @BeforeAll
    static void beforeAll(
            @Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @WithMockUser(username = "customer", roles = {"CUSTOMER"})
    @Test
    @Sql(scripts = "classpath:database/booking/delete-booking.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("create a new booking")
    void saveBooking_ValidRequestDto_Success() throws Exception {
        BookingRequestDto requestDto = new BookingRequestDto();
        requestDto.setCheckInDate(LocalDate.of(2024, 9, 15));
        requestDto.setCheckOutDate(LocalDate.of(2024, 9, 20));
        requestDto.setUserId(1L);
        requestDto.setAccommodationId(1L);
        requestDto.setStatus(BookingStatus.PENDING);

        BookingResponseDto responseDto = new BookingResponseDto();
        responseDto.setId(1L);
        responseDto.setUserId(requestDto.getUserId());
        responseDto.setAccommodationId(requestDto.getAccommodationId());
        responseDto.setStatus(requestDto.getStatus());
        responseDto.setCheckInDate(requestDto.getCheckInDate());
        responseDto.setCheckOutDate(requestDto.getCheckOutDate());

        given(bookingService.save(requestDto)).willReturn(responseDto);

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(post("/bookings")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        BookingResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookingResponseDto.class);

        Assertions.assertEquals(responseDto.getStatus(), actual.getStatus());
        Assertions.assertEquals(responseDto.getUserId(), actual.getUserId());
        Assertions.assertEquals(responseDto.getAccommodationId(), actual.getAccommodationId());
        Assertions.assertEquals(responseDto.getCheckInDate(), actual.getCheckInDate());
        Assertions.assertEquals(responseDto.getCheckOutDate(), actual.getCheckOutDate());
    }

    @WithMockUser(username = "customer", roles = {"CUSTOMER", "MANAGER"})
    @Sql(scripts = "classpath:database/booking/delete-booking.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getById_ValidBookingId_Success() throws Exception {
        Long bookingId = 1L;

        BookingResponseDto responseDto = new BookingResponseDto();
        responseDto.setId(bookingId);
        responseDto.setStatus(BookingStatus.PENDING);
        responseDto.setAccommodationId(1L);
        responseDto.setUserId(1L);
        responseDto.setCheckInDate(LocalDate.of(2024, 9, 15));
        responseDto.setCheckOutDate(LocalDate.of(2024, 9, 20));

        Mockito.when(bookingService.getById(bookingId)).thenReturn(responseDto);

        MvcResult result = mockMvc.perform(get("/bookings/{id}", bookingId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        BookingResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookingResponseDto.class);

        Assertions.assertEquals(responseDto.getId(), actual.getId());
        Assertions.assertEquals(responseDto.getUserId(), actual.getUserId());
        Assertions.assertEquals(responseDto.getAccommodationId(), actual.getAccommodationId());
        Assertions.assertEquals(responseDto.getStatus(), actual.getStatus());
        Assertions.assertEquals(responseDto.getCheckInDate(), actual.getCheckInDate());
        Assertions.assertEquals(responseDto.getCheckOutDate(), actual.getCheckOutDate());
    }

    @Test
    @WithMockUser(username = "customer", roles = {"CUSTOMER", "MANAGER"})
    @DisplayName("delete booking by id")
    void deleteBooking_ValidBookingId_Success() throws Exception {
        Long bookingId = 1L;

        Mockito.doNothing().when(bookingService).delete(bookingId);

        mockMvc.perform(delete("/bookings/{id}", bookingId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        Mockito.verify(bookingService, Mockito.times(1)).delete(bookingId);
    }

    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @Test
    @Sql(scripts = "classpath:database/booking/delete-booking.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Get bookings by user ID and status")
    void findByUserIdAndStatus_ValidUserIdAndStatus_Success() throws Exception {
        Long userId = 1L;

        BookingResponseDto responseDto = new BookingResponseDto();
        responseDto.setId(1L);
        responseDto.setStatus(BookingStatus.PENDING);
        responseDto.setAccommodationId(1L);
        responseDto.setUserId(userId);
        responseDto.setCheckInDate(LocalDate.of(2024, 9, 15));
        responseDto.setCheckOutDate(LocalDate.of(2024, 9, 20));

        List<BookingResponseDto> responseList = List.of(responseDto);

        String status = "PENDING";

        Mockito.when(bookingService.findByUserIdAndStatus(status, userId))
                .thenReturn(responseList);

        MvcResult result = mockMvc.perform(get("/bookings/{userId}/{status}", userId, status)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        List<BookingResponseDto> actualList = objectMapper.readValue(
                jsonResponse, new TypeReference<>() {});

        Assertions.assertEquals(1, actualList.size());
        Assertions.assertEquals(responseDto.getId(), actualList.get(0).getId());
        Assertions.assertEquals(responseDto.getUserId(), actualList.get(0).getUserId());
        Assertions.assertEquals(responseDto.getStatus(), actualList.get(0).getStatus());
    }
}
