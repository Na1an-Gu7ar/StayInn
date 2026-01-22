package com.stayinn.dto.Booking;

import com.stayinn.entities.BookingStatus;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingUpdateStatusDTO {
    
    @NotNull(message = "Status is required")
    private BookingStatus status;
    
}
