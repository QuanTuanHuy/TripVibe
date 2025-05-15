package com.booking.inventory.presentation.api;

import com.booking.inventory.application.dto.AvailabilitySearchRequest;
import com.booking.inventory.application.dto.BookingConfirmationRequest;
import com.booking.inventory.application.dto.RoomAvailabilityDto;
import com.booking.inventory.application.dto.RoomLockRequest;
import com.booking.inventory.application.service.InventoryApplicationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InventoryController.class)
class InventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private InventoryApplicationService inventoryService;

    @Test
    void searchAvailability_ShouldReturnAvailableRooms() throws Exception {
        // Arrange
        AvailabilitySearchRequest request = new AvailabilitySearchRequest();
        request.setPropertyId(1L);
        request.setStartDate(LocalDate.now());
        request.setEndDate(LocalDate.now().plusDays(2));
        request.setAdults(2);
        request.setChildren(0);

        List<RoomAvailabilityDto> availabilities = Arrays.asList(
                RoomAvailabilityDto.builder()
                        .id(1L)
                        .roomId(1L)
                        .roomNumber("101")
                        .roomName("Standard Room")
                        .propertyId(1L)
                        .propertyName("Test Hotel")
                        .roomTypeId(1L)
                        .roomTypeName("Standard")
                        .price(new BigDecimal("100.00"))
                        .date(LocalDate.now())
                        .build(),
                RoomAvailabilityDto.builder()
                        .id(2L)
                        .roomId(2L)
                        .roomNumber("102")
                        .roomName("Deluxe Room")
                        .propertyId(1L)
                        .propertyName("Test Hotel")
                        .roomTypeId(2L)
                        .roomTypeName("Deluxe")
                        .price(new BigDecimal("150.00"))
                        .date(LocalDate.now())
                        .build()
        );

        when(inventoryService.searchAvailability(any(AvailabilitySearchRequest.class)))
                .thenReturn(availabilities);

        // Act & Assert
        mockMvc.perform(post("/api/inventory/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].roomNumber").value("101"))
                .andExpect(jsonPath("$[0].price").value(100.00))
                .andExpect(jsonPath("$[1].roomNumber").value("102"))
                .andExpect(jsonPath("$[1].price").value(150.00));

        verify(inventoryService).searchAvailability(any(AvailabilitySearchRequest.class));
    }

    @Test
    void lockRoom_WhenSuccessful_ShouldReturnTrue() throws Exception {
        // Arrange
        RoomLockRequest request = new RoomLockRequest();
        request.setRoomId(1L);
        request.setStartDate(LocalDate.now());
        request.setEndDate(LocalDate.now().plusDays(2));
        request.setSessionId("test-session");

        when(inventoryService.lockRoom(any(RoomLockRequest.class))).thenReturn(true);

        // Act & Assert
        mockMvc.perform(post("/api/inventory/lock")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(inventoryService).lockRoom(any(RoomLockRequest.class));
    }

    @Test
    void lockRoom_WhenFailed_ShouldReturnBadRequest() throws Exception {
        // Arrange
        RoomLockRequest request = new RoomLockRequest();
        request.setRoomId(1L);
        request.setStartDate(LocalDate.now());
        request.setEndDate(LocalDate.now().plusDays(2));
        request.setSessionId("test-session");

        when(inventoryService.lockRoom(any(RoomLockRequest.class))).thenReturn(false);

        // Act & Assert
        mockMvc.perform(post("/api/inventory/lock")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("false"));

        verify(inventoryService).lockRoom(any(RoomLockRequest.class));
    }

    @Test
    void releaseRoomLock_ShouldCallService() throws Exception {
        // Arrange
        String sessionId = "test-session";
        doNothing().when(inventoryService).releaseRoomLock(anyString());

        // Act & Assert
        mockMvc.perform(delete("/api/inventory/lock/{sessionId}", sessionId))
                .andExpect(status().isOk());

        verify(inventoryService).releaseRoomLock(sessionId);
    }

    @Test
    void confirmBooking_WhenSuccessful_ShouldReturnTrue() throws Exception {
        // Arrange
        BookingConfirmationRequest request = new BookingConfirmationRequest();
        request.setRoomId(1L);
        request.setStartDate(LocalDate.now());
        request.setEndDate(LocalDate.now().plusDays(2));
        request.setSessionId("test-session");
        request.setBookingId("test-booking");

        when(inventoryService.confirmBooking(any(BookingConfirmationRequest.class))).thenReturn(true);

        // Act & Assert
        mockMvc.perform(post("/api/inventory/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(inventoryService).confirmBooking(any(BookingConfirmationRequest.class));
    }

    @Test
    void cancelBooking_ShouldCallService() throws Exception {
        // Arrange
        String bookingId = "test-booking";
        doNothing().when(inventoryService).cancelBooking(anyString());

        // Act & Assert
        mockMvc.perform(delete("/api/inventory/booking/{bookingId}", bookingId))
                .andExpect(status().isOk());

        verify(inventoryService).cancelBooking(bookingId);
    }
}
