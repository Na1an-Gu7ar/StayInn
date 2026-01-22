package com.stayinn.service;

import java.time.LocalDate;
import java.util.List;

import com.stayinn.dto.Booking.BookingAvailabilityDTO;
import com.stayinn.dto.Booking.BookingAvailabilityResponseDTO;
import com.stayinn.dto.Booking.BookingCalendarDTO;
import com.stayinn.dto.Booking.BookingCreateDTO;
import com.stayinn.dto.Booking.BookingDetailDTO;
import com.stayinn.dto.Booking.BookingResponseDTO;
import com.stayinn.dto.Booking.BookingSummaryDTO;
import com.stayinn.dto.Booking.BookingUpdateStatusDTO;
import com.stayinn.entities.BookingStatus;

public interface BookingService {
    
   
    BookingResponseDTO createBooking(BookingCreateDTO bookingCreateDTO);
    
    
    BookingResponseDTO getBookingById(Long id);
    
    
    BookingDetailDTO getBookingDetailById(Long id);
    
    
    List<BookingResponseDTO> getBookingsByUserId(Long userId);
    
    
    List<BookingResponseDTO> getBookingsByVillaId(Long villaId);
    
    
    List<BookingResponseDTO> getBookingsByStatus(BookingStatus status);
    
    
    List<BookingResponseDTO> getUpcomingBookings(Long userId);
    
    
    List<BookingResponseDTO> getPastBookings(Long userId);
    
    
    List<BookingResponseDTO> getActiveBookings(Long userId);
    
    
    BookingResponseDTO updateBookingStatus(Long id, BookingUpdateStatusDTO updateStatusDTO);
    
    
    BookingResponseDTO confirmBooking(Long id);
    
    
    BookingResponseDTO cancelBooking(Long id, String reason);
    
    
    BookingAvailabilityResponseDTO checkAvailability(BookingAvailabilityDTO availabilityDTO);
    
    
    List<BookingCalendarDTO> getVillaCalendar(Long villaId, LocalDate startDate, LocalDate endDate);
    
    
    BookingSummaryDTO getBookingSummary();
    
    
    BookingSummaryDTO getUserBookingSummary(Long userId);
    
    
    List<BookingResponseDTO> getAllBookings();
    
    
    void deleteBooking(Long id);
    
    
    int autoCompleteBookings();
}