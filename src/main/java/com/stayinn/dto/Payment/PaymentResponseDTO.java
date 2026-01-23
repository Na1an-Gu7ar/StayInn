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
public class PaymentResponseDTO {
    
    private Long id;
    private Long bookingId;
    private Double amount;
    private LocalDate paymentDate;
    private String paymentMethod;
    private String paymentGateway;
    private PaymentStatus status;
    private String transactionId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Simplified constructor
    public PaymentResponseDTO(Long id, Long bookingId, Double amount, 
                             LocalDate paymentDate, String paymentMethod, 
                             PaymentStatus status, String transactionId) {
        this.id = id;
        this.bookingId = bookingId;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.transactionId = transactionId;
    }
}
