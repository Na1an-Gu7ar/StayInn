package com.stayinn.dto.Booking;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// ========== BOOKING CREATE DTO ==========
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingCreateDTO {
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    @NotNull(message = "Villa ID is required")
    private Long villaId;
    
    @NotNull(message = "Check-in date is required")
    private String checkInDate;
    
    @NotNull(message = "Check-out date is required")
    private String checkOutDate;
    
}
