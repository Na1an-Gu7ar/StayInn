package com.stayinn.dto.Booking;

import java.time.LocalDate;

import com.stayinn.entities.BookingStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingCalendarDTO {
    
    private Long bookingId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private BookingStatus status;
    private String userName;
}