package com.stayinn.dto.Booking;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.stayinn.entities.BookingStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponseDTO {
    
    private Long id;
    private Long userId;
    private String userName;
    private String userEmail;
    private Long villaId;
    private String villaName;
    private String villaLocation;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer numberOfNights;
    private Double totalPrice;
    private BookingStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Simple constructor without villa/user details
    public BookingResponseDTO(Long id, Long userId, Long villaId, LocalDate checkInDate, 
                             LocalDate checkOutDate, Double totalPrice, BookingStatus status) {
        this.id = id;
        this.userId = userId;
        this.villaId = villaId;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.totalPrice = totalPrice;
        this.status = status;
    }
}