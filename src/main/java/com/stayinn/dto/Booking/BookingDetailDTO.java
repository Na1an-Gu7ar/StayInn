package com.stayinn.dto.Booking;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.stayinn.entities.BookingStatus;
import com.stayinn.entities.PaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDetailDTO {
    
    private Long id;
    
    // User Details
    private Long userId;
    private String userName;
    private String userEmail;
    private String userPhone;
    
    // Villa Details
    private Long villaId;
    private String villaName;
    private String villaLocation;
    private Double villaPrice;
    private String villaImage;
    
    // Booking Details
    private String checkInDate;
    private String checkOutDate;
    private Integer numberOfNights;
    private Double totalPrice;
    private BookingStatus status;
    
    // Payment Details
    private Long paymentId;
    private String paymentStatus;
    private String paymentMethod;
    
    // Timestamps
    private String createdAt;
    private String updatedAt;
}
