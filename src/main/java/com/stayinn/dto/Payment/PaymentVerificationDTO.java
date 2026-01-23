package com.stayinn.dto.Payment;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentVerificationDTO {
    
    @NotBlank(message = "Transaction ID is required")
    private String transactionId;
    
    @NotBlank(message = "Payment gateway is required")
    private String paymentGateway;
    
    private String signature; // For payment gateway signature verification
}