package com.stayinn.repository;

import com.stayinn.entities.Booking;
import com.stayinn.entities.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    
    /**
     * Find all bookings by user ID
     */
    List<Booking> findByUserId(Long userId);
    
    /**
     * Find all bookings by villa ID
     */
    List<Booking> findByVillaId(Long villaId);
    
    /**
     * Find bookings by user ID and status
     */
    List<Booking> findByUserIdAndStatus(Long userId, BookingStatus status);
    
    /**
     * Find bookings by villa ID and status
     */
    List<Booking> findByVillaIdAndStatus(Long villaId, BookingStatus status);
    
    /**
     * Find all bookings by status
     */
    List<Booking> findByStatus(BookingStatus status);
    
    /**
     * Check if villa is available for given dates
     * Returns bookings that overlap with requested dates
     * CRITICAL: Prevents double-booking
     */
    @Query("SELECT b FROM Booking b WHERE b.villa.id = :villaId " +
           "AND b.status IN ('PENDING', 'CONFIRMED') " +
           "AND ((b.checkInDate <= :checkOut AND b.checkOutDate > :checkIn))")
    List<Booking> findConflictingBookings(
            @Param("villaId") Long villaId,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut
    );
    
    /**
     * Find booking with user and villa details (eager loading)
     */
    @Query("SELECT b FROM Booking b " +
           "LEFT JOIN FETCH b.user " +
           "LEFT JOIN FETCH b.villa " +
           "WHERE b.id = :bookingId")
    Optional<Booking> findByIdWithDetails(@Param("bookingId") Long bookingId);
    
    /**
     * Find upcoming bookings for a user (check-in date in future)
     */
    @Query("SELECT b FROM Booking b WHERE b.user.id = :userId " +
           "AND b.checkInDate > :currentDate " +
           "AND b.status = 'CONFIRMED' " +
           "ORDER BY b.checkInDate ASC")
    List<Booking> findUpcomingBookingsByUser(
            @Param("userId") Long userId,
            @Param("currentDate") LocalDate currentDate
    );
    
    /**
     * Find past bookings for a user (check-out date in past)
     */
    @Query("SELECT b FROM Booking b WHERE b.user.id = :userId " +
           "AND b.checkOutDate < :currentDate " +
           "ORDER BY b.checkOutDate DESC")
    List<Booking> findPastBookingsByUser(
            @Param("userId") Long userId,
            @Param("currentDate") LocalDate currentDate
    );
    
    /**
     * Find active (current) bookings for a user
     */
    @Query("SELECT b FROM Booking b WHERE b.user.id = :userId " +
           "AND b.checkInDate <= :currentDate " +
           "AND b.checkOutDate > :currentDate " +
           "AND b.status = 'CONFIRMED'")
    List<Booking> findActiveBookingsByUser(
            @Param("userId") Long userId,
            @Param("currentDate") LocalDate currentDate
    );
    
    /**
     * Find all bookings for a villa in a date range
     */
    @Query("SELECT b FROM Booking b WHERE b.villa.id = :villaId " +
           "AND b.checkInDate >= :startDate " +
           "AND b.checkOutDate <= :endDate " +
           "ORDER BY b.checkInDate ASC")
    List<Booking> findVillaBookingsInDateRange(
            @Param("villaId") Long villaId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
    
    /**
     * Count total bookings by user
     */
    long countByUserId(Long userId);
    
    /**
     * Count total bookings by villa
     */
    long countByVillaId(Long villaId);
    
    /**
     * Count bookings by status
     */
    long countByStatus(BookingStatus status);
    
    /**
     * Check if user has already booked this villa (for rating validation)
     */
    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.user.id = :userId " +
           "AND b.villa.id = :villaId " +
           "AND b.status = 'COMPLETED'")
    boolean hasUserBookedVilla(@Param("userId") Long userId, @Param("villaId") Long villaId);
}