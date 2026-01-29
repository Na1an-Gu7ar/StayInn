package com.stayinn.service;

import java.time.LocalDate;
import java.util.List;

import com.stayinn.dto.Payment.PaymentCreateDTO;
import com.stayinn.dto.Payment.PaymentDetailDTO;
import com.stayinn.dto.Payment.PaymentResponseDTO;
import com.stayinn.dto.Payment.PaymentUpdateStatusDTO;
import com.stayinn.dto.Payment.PaymentVerificationDTO;
import com.stayinn.dto.Payment.RazorpayOrderResponse;
import com.stayinn.dto.Payment.RazorpayVerificationDTO;
import com.stayinn.dto.Payment.RefundRequestDTO;
import com.stayinn.dto.Payment.SimplePaymentDTO;
import com.stayinn.entities.PaymentStatus;

public interface PaymentService {
    
    /**
     * Create a new payment (initiate payment)
     * @param paymentCreateDTO payment details
     * @return created payment with PENDING status
     * @throws RuntimeException if booking not found or payment already exists
     */
    PaymentResponseDTO createPayment(PaymentCreateDTO paymentCreateDTO);
    
    /**
     * Get payment by ID
     * @param id payment ID
     * @return payment details
     * @throws RuntimeException if payment not found
     */
    PaymentResponseDTO getPaymentById(Long id);
    
    /**
     * Get detailed payment information
     * @param id payment ID
     * @return detailed payment with booking and user info
     * @throws RuntimeException if payment not found
     */
    PaymentDetailDTO getPaymentDetailById(Long id);
    
    /**
     * Get payment by booking ID
     * @param bookingId booking ID
     * @return payment for the booking
     * @throws RuntimeException if payment not found
     */
    PaymentResponseDTO getPaymentByBookingId(Long bookingId);
    
    /**
     * Get all payments by user ID
     * @param userId user ID
     * @return list of user's payments
     */
    List<PaymentResponseDTO> getPaymentsByUserId(Long userId);
    
    /**
     * Get simple payments for user (for display)
     * @param userId user ID
     * @return list of simple payment DTOs
     */
    List<SimplePaymentDTO> getSimplePaymentsByUserId(Long userId);
    
    /**
     * Get all payments by villa ID
     * @param villaId villa ID
     * @return list of payments for the villa
     */
    List<PaymentResponseDTO> getPaymentsByVillaId(Long villaId);
    
    /**
     * Update payment status
     * @param id payment ID
     * @param updateStatusDTO new status and transaction details
     * @return updated payment
     * @throws RuntimeException if payment not found
     */
    PaymentResponseDTO updatePaymentStatus(Long id, PaymentUpdateStatusDTO updateStatusDTO);
    
    /**
     * Complete payment (mark as COMPLETED)
     * Called after successful payment gateway callback
     * @param id payment ID
     * @param transactionId transaction ID from gateway
     * @return completed payment
     * @throws RuntimeException if payment not found or already completed
     */
    PaymentResponseDTO completePayment(Long id, String transactionId);
    
    /**
     * Mark payment as failed
     * @param id payment ID
     * @param reason failure reason
     * @return failed payment
     * @throws RuntimeException if payment not found
     */
    PaymentResponseDTO failPayment(Long id, String reason);
    
    /**
     * Process refund for a payment
     * @param refundRequestDTO refund details
     * @return refunded payment
     * @throws RuntimeException if payment not found or cannot be refunded
     */
    PaymentResponseDTO processRefund(RefundRequestDTO refundRequestDTO);
    
    /**
     * Verify payment with payment gateway
     * @param verificationDTO verification details
     * @return true if payment is verified, false otherwise
     */
    boolean verifyPayment(PaymentVerificationDTO verificationDTO);
    
    /**
     * Get payments by status
     * @param status payment status
     * @return list of payments with specified status
     */
    List<PaymentResponseDTO> getPaymentsByStatus(PaymentStatus status);
    
    /**
     * Get payments by payment method
     * @param paymentMethod payment method
     * @return list of payments with specified method
     */
    List<PaymentResponseDTO> getPaymentsByPaymentMethod(String paymentMethod);
    
    /**
     * Get successful payments
     * @return list of completed payments
     */
    List<PaymentResponseDTO> getSuccessfulPayments();
    
    /**
     * Get failed payments
     * @return list of failed payments
     */
    List<PaymentResponseDTO> getFailedPayments();
    
    /**
     * Get recent payments (last N days)
     * @param days number of days
     * @return list of recent payments
     */
    List<PaymentResponseDTO> getRecentPayments(int days);
    
    /**
     * Get payment statistics
     * @return payment statistics
     */
//    PaymentStatisticsDTO getPaymentStatistics();
    
    /**
     * Get revenue report for date range
     * @param startDate start date
     * @param endDate end date
     * @return revenue report
     */
//    RevenueReportDTO getRevenueReport(LocalDate startDate, LocalDate endDate);
    
    /**
     * Calculate total revenue
     * @return total revenue from completed payments
     */
    Double calculateTotalRevenue();
    
    /**
     * Get all payments (Admin only)
     * @return list of all payments
     */
    List<PaymentResponseDTO> getAllPayments();
    
    /**
     * Delete payment (Admin only)
     * @param id payment ID
     * @throws RuntimeException if payment not found
     */
    void deletePayment(Long id);
    
    /**
     * Get total payment count
     * @return number of payments
     */
    long getTotalPaymentCount();
    
    // ========== RAZORPAY-SPECIFIC METHODS ==========
    
    /**
     * Create Razorpay order for booking
     * @param bookingId booking ID
     * @return Razorpay order details
     * @throws Exception if order creation fails
     */
    RazorpayOrderResponse createRazorpayOrder(Long bookingId) throws Exception;
    
    /**
     * Verify and capture Razorpay payment
     * @param verificationDTO Razorpay verification details
     * @return completed payment
     * @throws Exception if verification fails
     */
    PaymentResponseDTO verifyAndCapturePayment(RazorpayVerificationDTO verificationDTO) throws Exception;
}