package com.stayinn.dto.Payment;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefundRequestDTO {
    
    @NotNull(message = "Payment ID is required")
    private Long paymentId;
    
    @NotNull(message = "Refund amount is required")
    @DecimalMin(value = "0.01", message = "Refund amount must be greater than 0")
    private Double refundAmount;
    
    @NotBlank(message = "Refund reason is required")
    @Size(min = 10, max = 500, message = "Reason must be between 10 and 500 characters")
    private String reason;
}
