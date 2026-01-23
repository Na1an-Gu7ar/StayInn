package com.stayinn.dto.Payment;

import com.stayinn.entities.PaymentStatus;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentUpdateStatusDTO {
    
    @NotNull(message = "Status is required")
    private PaymentStatus status;
    
    private String transactionId;
    
    private String failureReason; // For failed payments
}