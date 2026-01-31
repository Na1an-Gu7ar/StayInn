package com.stayinn.dto.Booking;

import java.time.LocalDate;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingAvailabilityDTO {
    
    @NotNull(message = "Villa ID is required")
    private Long villaId;
    
    @NotNull(message = "Check-in date is required")
    private String checkInDate;
    
    @NotNull(message = "Check-out date is required")
    private String checkOutDate;
}
