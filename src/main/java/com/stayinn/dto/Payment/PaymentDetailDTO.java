package com.stayinn.dto.Payment;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.stayinn.entities.PaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDetailDTO {
    
    private Long id;
    
    // Payment details
    private Double amount;
    private LocalDate paymentDate;
    private String paymentMethod;
    private String paymentGateway;
    private PaymentStatus status;
    private String transactionId;
    
    // Booking details
    private Long bookingId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer numberOfNights;
    
    // User details
    private Long userId;
    private String userName;
    private String userEmail;
    
    // Villa details
    private Long villaId;
    private String villaName;
    private String villaAddress;
    
    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
