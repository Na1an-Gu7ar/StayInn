package com.stayinn.repository;

import com.stayinn.entities.Payment;
import com.stayinn.entities.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    /**
     * Find payment by booking ID
     */
    Optional<Payment> findByBookingId(Long bookingId);
    
    /**
     * Check if payment exists for booking
     */
    boolean existsByBookingId(Long bookingId);
    
    /**
     * Find payments by status
     */
    List<Payment> findByStatus(PaymentStatus status);
    
    /**
     * Find payments by payment method
     */
    List<Payment> findByPaymentMethod(String paymentMethod);
    
    /**
     * Find payment by transaction ID
     */
    Optional<Payment> findByTransactionId(String transactionId);
    
    /**
     * Find payments in a date range
     */
    List<Payment> findByPaymentDateBetween(LocalDate startDate, LocalDate endDate);
    
    /**
     * Find payment with booking details (eager loading)
     */
    @Query("SELECT p FROM Payment p " +
           "LEFT JOIN FETCH p.booking b " +
           "LEFT JOIN FETCH b.user " +
           "LEFT JOIN FETCH b.villa " +
           "WHERE p.id = :paymentId")
    Optional<Payment> findByIdWithDetails(@Param("paymentId") Long paymentId);
    
    /**
     * Find all payments by user (through booking)
     */
    @Query("SELECT p FROM Payment p WHERE p.booking.user.id = :userId " +
           "ORDER BY p.paymentDate DESC")
    List<Payment> findByUserId(@Param("userId") Long userId);
    
    /**
     * Find all payments by villa (through booking)
     */
    @Query("SELECT p FROM Payment p WHERE p.booking.villa.id = :villaId " +
           "ORDER BY p.paymentDate DESC")
    List<Payment> findByVillaId(@Param("villaId") Long villaId);
    
    /**
     * Find successful payments (COMPLETED status)
     */
    @Query("SELECT p FROM Payment p WHERE p.status = 'COMPLETED' " +
           "ORDER BY p.paymentDate DESC")
    List<Payment> findSuccessfulPayments();
    
    /**
     * Find failed payments
     */
    @Query("SELECT p FROM Payment p WHERE p.status = 'FAILED' " +
           "ORDER BY p.paymentDate DESC")
    List<Payment> findFailedPayments();
    
    /**
     * Calculate total revenue (sum of completed payments)
     */
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = 'COMPLETED'")
    Double calculateTotalRevenue();
    
    /**
     * Calculate revenue in date range
     */
    @Query("SELECT SUM(p.amount) FROM Payment p " +
           "WHERE p.status = 'COMPLETED' " +
           "AND p.paymentDate BETWEEN :startDate AND :endDate")
    Double calculateRevenueInDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
    
    /**
     * Count payments by status
     */
    long countByStatus(PaymentStatus status);
    
    /**
     * Count payments by payment method
     */
    long countByPaymentMethod(String paymentMethod);
    
    /**
     * Find recent payments (last N days)
     */
    @Query("SELECT p FROM Payment p WHERE p.paymentDate >= :date " +
           "ORDER BY p.paymentDate DESC")
    List<Payment> findRecentPayments(@Param("date") LocalDate date);
    
    /**
     * Get payment method distribution
     */
    @Query("SELECT p.paymentMethod, COUNT(p) FROM Payment p " +
           "WHERE p.status = 'COMPLETED' " +
           "GROUP BY p.paymentMethod")
    List<Object[]> getPaymentMethodDistribution();
}