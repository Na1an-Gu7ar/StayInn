package com.stayinn.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stayinn.dto.Booking.BookingAvailabilityDTO;
import com.stayinn.dto.Booking.BookingAvailabilityResponseDTO;
import com.stayinn.dto.Booking.BookingCalendarDTO;
import com.stayinn.dto.Booking.BookingCreateDTO;
import com.stayinn.dto.Booking.BookingDetailDTO;
import com.stayinn.dto.Booking.BookingResponseDTO;
import com.stayinn.dto.Booking.BookingSummaryDTO;
import com.stayinn.dto.Booking.BookingUpdateStatusDTO;
import com.stayinn.entities.Booking;
import com.stayinn.entities.BookingStatus;
import com.stayinn.entities.User;
import com.stayinn.entities.Villa;
import com.stayinn.repository.BookingRepository;
import com.stayinn.repository.UserRepository;
import com.stayinn.repository.VillaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BookingServiceImpl implements BookingService {
    
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final VillaRepository villaRepository;
    
    @Override
    public BookingResponseDTO createBooking(BookingCreateDTO bookingCreateDTO) {
        log.info("Creating booking for user {} and villa {}", 
                bookingCreateDTO.getUserId(), bookingCreateDTO.getVillaId());
        
        // Validate dates
        validateBookingDates(bookingCreateDTO.getCheckInDate(), bookingCreateDTO.getCheckOutDate());
        
        // Fetch user
        User user = userRepository.findById(bookingCreateDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + bookingCreateDTO.getUserId()));
        
        // Fetch villa
        Villa villa = villaRepository.findById(bookingCreateDTO.getVillaId())
                .orElseThrow(() -> new RuntimeException("Villa not found with ID: " + bookingCreateDTO.getVillaId()));
        
        // Check availability
        List<Booking> conflictingBookings = bookingRepository.findConflictingBookings(
                villa.getId(), 
                bookingCreateDTO.getCheckInDate(), 
                bookingCreateDTO.getCheckOutDate()
        );
        
        if (!conflictingBookings.isEmpty()) {
            throw new RuntimeException("Villa is not available for the selected dates");
        }
        
        // Calculate number of nights and total price
        long numberOfNights = ChronoUnit.DAYS.between(
                bookingCreateDTO.getCheckInDate(), 
                bookingCreateDTO.getCheckOutDate()
        );
        Double totalPrice = numberOfNights * villa.getPricePerNight();
        
        // Create booking
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setVilla(villa);
        booking.setCheckInDate(bookingCreateDTO.getCheckInDate());
        booking.setCheckOutDate(bookingCreateDTO.getCheckOutDate());
        booking.setTotalPrice(totalPrice);
        booking.setStatus(BookingStatus.PENDING);
        
        Booking savedBooking = bookingRepository.save(booking);
        log.info("Booking created successfully with ID: {}", savedBooking.getId());
        
        return mapToResponseDTO(savedBooking);
    }
    
    @Override
    @Transactional(readOnly = true)
    public BookingResponseDTO getBookingById(Long id) {
        log.info("Fetching booking with ID: {}", id);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + id));
        return mapToResponseDTO(booking);
    }
    
    @Override
    @Transactional(readOnly = true)
    public BookingDetailDTO getBookingDetailById(Long id) {
        log.info("Fetching detailed booking with ID: {}", id);
        Booking booking = bookingRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + id));
        return mapToDetailDTO(booking);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDTO> getBookingsByUserId(Long userId) {
        log.info("Fetching bookings for user ID: {}", userId);
        return bookingRepository.findByUserId(userId).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDTO> getBookingsByVillaId(Long villaId) {
        log.info("Fetching bookings for villa ID: {}", villaId);
        return bookingRepository.findByVillaId(villaId).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDTO> getBookingsByStatus(BookingStatus status) {
        log.info("Fetching bookings with status: {}", status);
        return bookingRepository.findByStatus(status).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDTO> getUpcomingBookings(Long userId) {
        log.info("Fetching upcoming bookings for user ID: {}", userId);
        return bookingRepository.findUpcomingBookingsByUser(userId, LocalDate.now()).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDTO> getPastBookings(Long userId) {
        log.info("Fetching past bookings for user ID: {}", userId);
        return bookingRepository.findPastBookingsByUser(userId, LocalDate.now()).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDTO> getActiveBookings(Long userId) {
        log.info("Fetching active bookings for user ID: {}", userId);
        return bookingRepository.findActiveBookingsByUser(userId, LocalDate.now()).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public BookingResponseDTO updateBookingStatus(Long id, BookingUpdateStatusDTO updateStatusDTO) {
        log.info("Updating booking {} status to {}", id, updateStatusDTO.getStatus());
        
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + id));
        
        // Validate status transition
        validateStatusTransition(booking.getStatus(), updateStatusDTO.getStatus());
        
        booking.setStatus(updateStatusDTO.getStatus());
        Booking updatedBooking = bookingRepository.save(booking);
        
        log.info("Booking status updated successfully");
        return mapToResponseDTO(updatedBooking);
    }
    
    @Override
    public BookingResponseDTO confirmBooking(Long id) {
        log.info("Confirming booking with ID: {}", id);
        
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + id));
        
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new RuntimeException("Only PENDING bookings can be confirmed");
        }
        
        booking.setStatus(BookingStatus.CONFIRMED);
        Booking confirmedBooking = bookingRepository.save(booking);
        
        log.info("Booking confirmed successfully");
        return mapToResponseDTO(confirmedBooking);
    }
    
    @Override
    public BookingResponseDTO cancelBooking(Long id, String reason) {
        log.info("Cancelling booking with ID: {}", id);
        
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + id));
        
        // Check if booking can be cancelled
        if (booking.getStatus() == BookingStatus.CANCELLED || 
            booking.getStatus() == BookingStatus.CONFIRMED) {
            throw new RuntimeException("Booking cannot be cancelled");
        }
        
        // Check if check-in date has passed
        if (booking.getCheckInDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Cannot cancel booking after check-in date");
        }
        
        booking.setStatus(BookingStatus.CANCELLED);
        Booking cancelledBooking = bookingRepository.save(booking);
        
        log.info("Booking cancelled successfully. Reason: {}", reason);
        return mapToResponseDTO(cancelledBooking);
    }
    
    @Override
    @Transactional(readOnly = true)
    public BookingAvailabilityResponseDTO checkAvailability(BookingAvailabilityDTO availabilityDTO) {
        log.info("Checking availability for villa {} from {} to {}", 
                availabilityDTO.getVillaId(), 
                availabilityDTO.getCheckInDate(), 
                availabilityDTO.getCheckOutDate());
        
        // Validate dates
        validateBookingDates(availabilityDTO.getCheckInDate(), availabilityDTO.getCheckOutDate());
        
        // Fetch villa
        Villa villa = villaRepository.findById(availabilityDTO.getVillaId())
                .orElseThrow(() -> new RuntimeException("Villa not found"));
        
        // Check for conflicts
        List<Booking> conflictingBookings = bookingRepository.findConflictingBookings(
                availabilityDTO.getVillaId(),
                availabilityDTO.getCheckInDate(),
                availabilityDTO.getCheckOutDate()
        );
        
        boolean available = conflictingBookings.isEmpty();
        
        // Calculate estimated price
        long numberOfNights = ChronoUnit.DAYS.between(
                availabilityDTO.getCheckInDate(), 
                availabilityDTO.getCheckOutDate()
        );
        Double estimatedPrice = available ? numberOfNights * villa.getPricePerNight() : null;
        
        String message = available ? 
                "Villa is available for selected dates" : 
                "Villa is not available for selected dates";
        
        return new BookingAvailabilityResponseDTO(
        );
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<BookingCalendarDTO> getVillaCalendar(Long villaId, LocalDate startDate, LocalDate endDate) {
        log.info("Fetching calendar for villa {} from {} to {}", villaId, startDate, endDate);
        
        return bookingRepository.findVillaBookingsInDateRange(villaId, startDate, endDate).stream()
                .map(booking -> new BookingCalendarDTO(
                        booking.getId(),
                        booking.getCheckInDate(),
                        booking.getCheckOutDate(),
                        booking.getStatus(),
                        booking.getUser().getName()
                ))
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public BookingSummaryDTO getBookingSummary() {
        log.info("Generating booking summary");
        
        long totalBookings = bookingRepository.count();
        long pendingBookings = bookingRepository.countByStatus(BookingStatus.PENDING);
        long confirmedBookings = bookingRepository.countByStatus(BookingStatus.CONFIRMED);
        long completedBookings = bookingRepository.countByStatus(BookingStatus.CONFIRMED);
        long cancelledBookings = bookingRepository.countByStatus(BookingStatus.CANCELLED);
        
        // Calculate total revenue from completed bookings
        List<Booking> completedList = bookingRepository.findByStatus(BookingStatus.CONFIRMED);
        Double totalRevenue = completedList.stream()
                .mapToDouble(Booking::getTotalPrice)
                .sum();
        
        return new BookingSummaryDTO(
                totalBookings, 
                pendingBookings, 
                confirmedBookings, 
                completedBookings, 
                cancelledBookings, 
                totalRevenue
        );
    }
    
    @Override
    @Transactional(readOnly = true)
    public BookingSummaryDTO getUserBookingSummary(Long userId) {
        log.info("Generating booking summary for user {}", userId);
        
        List<Booking> userBookings = bookingRepository.findByUserId(userId);
        
        long totalBookings = userBookings.size();
        long pendingBookings = userBookings.stream().filter(b -> b.getStatus() == BookingStatus.PENDING).count();
        long confirmedBookings = userBookings.stream().filter(b -> b.getStatus() == BookingStatus.CONFIRMED).count();
        long completedBookings = userBookings.stream().filter(b -> b.getStatus() == BookingStatus.CONFIRMED).count();
        long cancelledBookings = userBookings.stream().filter(b -> b.getStatus() == BookingStatus.CANCELLED).count();
        
        Double totalSpent = userBookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.CONFIRMED)
                .mapToDouble(Booking::getTotalPrice)
                .sum();
        
        return new BookingSummaryDTO(
                totalBookings, 
                pendingBookings, 
                confirmedBookings, 
                completedBookings, 
                cancelledBookings, 
                totalSpent
        );
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDTO> getAllBookings() {
        log.info("Fetching all bookings");
        return bookingRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public void deleteBooking(Long id) {
        log.info("Deleting booking with ID: {}", id);
        
        if (!bookingRepository.existsById(id)) {
            throw new RuntimeException("Booking not found with ID: " + id);
        }
        
        bookingRepository.deleteById(id);
        log.info("Booking deleted successfully");
    }
    
    @Override
    public int autoCompleteBookings() {
        log.info("Running auto-complete bookings task");
        
        List<Booking> bookingsToComplete = bookingRepository.findByStatus(BookingStatus.CONFIRMED).stream()
                .filter(booking -> booking.getCheckOutDate().isBefore(LocalDate.now()))
                .collect(Collectors.toList());
        
        bookingsToComplete.forEach(booking -> booking.setStatus(BookingStatus.CONFIRMED));
        bookingRepository.saveAll(bookingsToComplete);
        
        log.info("Auto-completed {} bookings", bookingsToComplete.size());
        return bookingsToComplete.size();
    }
    
    // ========== HELPER METHODS ==========
    
    private void validateBookingDates(LocalDate checkIn, LocalDate checkOut) {
        if (checkIn == null || checkOut == null) {
            throw new RuntimeException("Check-in and check-out dates are required");
        }
        
        if (checkIn.isBefore(LocalDate.now())) {
            throw new RuntimeException("Check-in date cannot be in the past");
        }
        
        if (checkOut.isBefore(checkIn) || checkOut.isEqual(checkIn)) {
            throw new RuntimeException("Check-out date must be after check-in date");
        }
        
        long numberOfNights = ChronoUnit.DAYS.between(checkIn, checkOut);
        if (numberOfNights < 1) {
            throw new RuntimeException("Minimum booking is 1 night");
        }
    }
    
    private void validateStatusTransition(BookingStatus currentStatus, BookingStatus newStatus) {
        // Define valid transitions
        if (currentStatus == BookingStatus.CANCELLED || currentStatus == BookingStatus.CONFIRMED) {
            throw new RuntimeException("Cannot change status of cancelled or completed bookings");
        }
        
        if (currentStatus == BookingStatus.PENDING && newStatus == BookingStatus.CONFIRMED) {
            throw new RuntimeException("Cannot complete a pending booking directly");
        }
    }
    
    private BookingResponseDTO mapToResponseDTO(Booking booking) {
        long numberOfNights = ChronoUnit.DAYS.between(
                booking.getCheckInDate(), 
                booking.getCheckOutDate()
        );
        
        BookingResponseDTO dto = new BookingResponseDTO();
        dto.setId(booking.getId());
        dto.setUserId(booking.getUser().getId());
        dto.setUserName(booking.getUser().getName());
        dto.setUserEmail(booking.getUser().getEmail());
        dto.setVillaId(booking.getVilla().getId());
        dto.setVillaName(booking.getVilla().getName());
//        dto.setVillaLocation(booking.getVilla().getLocation());
        dto.setCheckInDate(booking.getCheckInDate());
        dto.setCheckOutDate(booking.getCheckOutDate());
        dto.setNumberOfNights((int) numberOfNights);
        dto.setTotalPrice(booking.getTotalPrice());
        dto.setStatus(booking.getStatus());
        dto.setCreatedAt(booking.getCreatedAt());
        dto.setUpdatedAt(booking.getUpdatedAt());
        
        return dto;
    }
    
    private BookingDetailDTO mapToDetailDTO(Booking booking) {
        long numberOfNights = ChronoUnit.DAYS.between(
                booking.getCheckInDate(), 
                booking.getCheckOutDate()
        );
        
        BookingDetailDTO dto = new BookingDetailDTO();
        dto.setId(booking.getId());
        dto.setUserId(booking.getUser().getId());
        dto.setUserName(booking.getUser().getName());
        dto.setUserEmail(booking.getUser().getEmail());
        dto.setVillaId(booking.getVilla().getId());
        dto.setVillaName(booking.getVilla().getName());
//        dto.setVillaLocation(booking.getVilla().getLocation());
        dto.setVillaPrice(booking.getVilla().getPricePerNight());
        dto.setCheckInDate(booking.getCheckInDate());
        dto.setCheckOutDate(booking.getCheckOutDate());
        dto.setNumberOfNights((int) numberOfNights);
        dto.setTotalPrice(booking.getTotalPrice());
        dto.setStatus(booking.getStatus());
        dto.setCreatedAt(booking.getCreatedAt());
        dto.setUpdatedAt(booking.getUpdatedAt());
        
        // Payment details (if exists)
        if (booking.getPayment() != null) {
            dto.setPaymentId(booking.getPayment().getId());
//            dto.setPaymentStatus(booking.getPayment().getStatus());
            dto.setPaymentMethod(booking.getPayment().getPaymentMethod());
        }
        
        return dto;
    }
}