package com.stayinn.dto.Payment;

import java.time.LocalDate;

import com.stayinn.entities.PaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimplePaymentDTO {
    
    private Long id;
    private Double amount;
    private LocalDate paymentDate;
    private String paymentMethod;
    private PaymentStatus status;
    private String villaName;
}
