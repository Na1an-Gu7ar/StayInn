package com.stayinn.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stayinn.dto.Booking.BookingAvailabilityDTO;
import com.stayinn.dto.Booking.BookingAvailabilityResponseDTO;
import com.stayinn.dto.Booking.BookingCalendarDTO;
import com.stayinn.dto.Booking.BookingCreateDTO;
import com.stayinn.dto.Booking.BookingDetailDTO;
import com.stayinn.dto.Booking.BookingResponseDTO;
import com.stayinn.dto.Booking.BookingSummaryDTO;
import com.stayinn.dto.Booking.BookingUpdateStatusDTO;
import com.stayinn.entities.BookingStatus;
import com.stayinn.service.BookingService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BookingController {
    
    private final BookingService bookingService;
    
    // ========== PUBLIC/USER ENDPOINTS ==========
    
    /**
     * Create a new booking
     * POST /api/bookings
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createBooking(@Valid @RequestBody BookingCreateDTO bookingCreateDTO) {
        try {
            BookingResponseDTO booking = bookingService.createBooking(bookingCreateDTO);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Booking created successfully");
            response.put("data", booking);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    /**
     * Check villa availability
     * POST /api/bookings/check-availability
     */
    @PostMapping("/check-availability")
    public ResponseEntity<Map<String, Object>> checkAvailability(@Valid @RequestBody BookingAvailabilityDTO availabilityDTO) {
        try {
            BookingAvailabilityResponseDTO availability = bookingService.checkAvailability(availabilityDTO);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", availability);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    /**
     * Get booking by ID
     * GET /api/bookings/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getBookingById(@PathVariable Long id) {
        try {
            BookingResponseDTO booking = bookingService.getBookingById(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", booking);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
    
    /**
     * Get detailed booking information
     * GET /api/bookings/{id}/details
     */
    @GetMapping("/{id}/details")
    public ResponseEntity<Map<String, Object>> getBookingDetails(@PathVariable Long id) {
        try {
            BookingDetailDTO bookingDetail = bookingService.getBookingDetailById(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", bookingDetail);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
    
    /**
     * Get all bookings by user ID
     * GET /api/bookings/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserBookings(@PathVariable Long userId) {
        List<BookingResponseDTO> bookings = bookingService.getBookingsByUserId(userId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("count", bookings.size());
        response.put("data", bookings);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get upcoming bookings for a user
     * GET /api/bookings/user/{userId}/upcoming
     */
    @GetMapping("/user/{userId}/upcoming")
    public ResponseEntity<Map<String, Object>> getUpcomingBookings(@PathVariable Long userId) {
        List<BookingResponseDTO> bookings = bookingService.getUpcomingBookings(userId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("count", bookings.size());
        response.put("data", bookings);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get past bookings for a user
     * GET /api/bookings/user/{userId}/past
     */
    @GetMapping("/user/{userId}/past")
    public ResponseEntity<Map<String, Object>> getPastBookings(@PathVariable Long userId) {
        List<BookingResponseDTO> bookings = bookingService.getPastBookings(userId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("count", bookings.size());
        response.put("data", bookings);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get active bookings for a user
     * GET /api/bookings/user/{userId}/active
     */
    @GetMapping("/user/{userId}/active")
    public ResponseEntity<Map<String, Object>> getActiveBookings(@PathVariable Long userId) {
        List<BookingResponseDTO> bookings = bookingService.getActiveBookings(userId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("count", bookings.size());
        response.put("data", bookings);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get user booking summary
     * GET /api/bookings/user/{userId}/summary
     */
    @GetMapping("/user/{userId}/summary")
    public ResponseEntity<Map<String, Object>> getUserBookingSummary(@PathVariable Long userId) {
        BookingSummaryDTO summary = bookingService.getUserBookingSummary(userId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", summary);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get all bookings for a villa
     * GET /api/bookings/villa/{villaId}
     */
    @GetMapping("/villa/{villaId}")
    public ResponseEntity<Map<String, Object>> getVillaBookings(@PathVariable Long villaId) {
        List<BookingResponseDTO> bookings = bookingService.getBookingsByVillaId(villaId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("count", bookings.size());
        response.put("data", bookings);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get villa calendar (bookings in date range)
     * GET /api/bookings/villa/{villaId}/calendar?startDate=2024-01-01&endDate=2024-12-31
     */
    @GetMapping("/villa/{villaId}/calendar")
    public ResponseEntity<Map<String, Object>> getVillaCalendar(
            @PathVariable Long villaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        List<BookingCalendarDTO> calendar = bookingService.getVillaCalendar(villaId, startDate, endDate);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("count", calendar.size());
        response.put("data", calendar);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Confirm booking (after payment)
     * PATCH /api/bookings/{id}/confirm
     */
    @PatchMapping("/{id}/confirm")
    public ResponseEntity<Map<String, Object>> confirmBooking(@PathVariable Long id) {
        try {
            BookingResponseDTO booking = bookingService.confirmBooking(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Booking confirmed successfully");
            response.put("data", booking);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    /**
     * Cancel booking
     * PATCH /api/bookings/{id}/cancel
     */
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Map<String, Object>> cancelBooking(
            @PathVariable Long id,
            @RequestParam(required = false) String reason) {
        try {
            BookingResponseDTO booking = bookingService.cancelBooking(id, reason);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Booking cancelled successfully");
            response.put("data", booking);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    /**
     * Update booking status
     * PATCH /api/bookings/{id}/status
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> updateBookingStatus(
            @PathVariable Long id,
            @Valid @RequestBody BookingUpdateStatusDTO updateStatusDTO) {
        try {
            BookingResponseDTO booking = bookingService.updateBookingStatus(id, updateStatusDTO);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Booking status updated successfully");
            response.put("data", booking);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    // ========== ADMIN ENDPOINTS ==========
    
    /**
     * Get all bookings (Admin only)
     * GET /api/bookings
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllBookings() {
        List<BookingResponseDTO> bookings = bookingService.getAllBookings();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("count", bookings.size());
        response.put("data", bookings);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get bookings by status (Admin only)
     * GET /api/bookings/status/{status}
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<Map<String, Object>> getBookingsByStatus(@PathVariable BookingStatus status) {
        List<BookingResponseDTO> bookings = bookingService.getBookingsByStatus(status);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("count", bookings.size());
        response.put("data", bookings);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get booking summary/statistics (Admin only)
     * GET /api/bookings/summary
     */
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getBookingSummary() {
        BookingSummaryDTO summary = bookingService.getBookingSummary();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", summary);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Delete booking (Admin only)
     * DELETE /api/bookings/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteBooking(@PathVariable Long id) {
        try {
            bookingService.deleteBooking(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Booking deleted successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
    
    /**
     * Auto-complete past bookings (Admin/Scheduled task)
     * POST /api/bookings/auto-complete
     */
    @PostMapping("/auto-complete")
    public ResponseEntity<Map<String, Object>> autoCompleteBookings() {
        int completedCount = bookingService.autoCompleteBookings();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", completedCount + " bookings auto-completed");
        response.put("count", completedCount);
        return ResponseEntity.ok(response);
    }
}