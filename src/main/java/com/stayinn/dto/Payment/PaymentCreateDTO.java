package com.stayinn.dto.Payment;



import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// ========== PAYMENT CREATE DTO ==========
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCreateDTO {
    
    @NotNull(message = "Booking ID is required")
    private Long bookingId;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private Double amount;
    
    @NotBlank(message = "Payment method is required")
    @Pattern(regexp = "CREDIT_CARD|DEBIT_CARD|UPI|NET_BANKING|WALLET|PAYPAL", 
             message = "Invalid payment method")
    private String paymentMethod;
    
    @NotBlank(message = "Payment gateway is required")
    private String paymentGateway; // "STRIPE", "RAZORPAY", "PAYPAL"
}