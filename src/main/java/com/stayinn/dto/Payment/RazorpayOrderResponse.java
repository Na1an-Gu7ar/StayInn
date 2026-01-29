package com.stayinn.dto.Payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RazorpayOrderResponse {
    
    private String orderId;
    private String currency;
    private Integer amount; // Amount in paise
    private String keyId;
    private Long bookingId;
    private Long paymentId;
    private String userName;
    private String userEmail;
    private String userPhone;
    private String villaName;
    private String companyName;
}
