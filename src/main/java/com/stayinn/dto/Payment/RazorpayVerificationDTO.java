package com.stayinn.dto.Payment;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RazorpayVerificationDTO {
    
    @NotBlank(message = "Order ID is required")
    private String razorpayOrderId;
    
    @NotBlank(message = "Payment ID is required")
    private String razorpayPaymentId;
    
    @NotBlank(message = "Signature is required")
    private String razorpaySignature;
}
