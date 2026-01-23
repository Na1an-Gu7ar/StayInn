package com.stayinn.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stayinn.dto.Payment.PaymentCreateDTO;
import com.stayinn.dto.Payment.PaymentDetailDTO;
import com.stayinn.dto.Payment.PaymentResponseDTO;
import com.stayinn.dto.Payment.PaymentUpdateStatusDTO;
import com.stayinn.dto.Payment.PaymentVerificationDTO;
import com.stayinn.dto.Payment.RefundRequestDTO;
import com.stayinn.dto.Payment.SimplePaymentDTO;
import com.stayinn.entities.Booking;
import com.stayinn.entities.Payment;
import com.stayinn.entities.PaymentStatus;
import com.stayinn.repository.BookingRepository;
import com.stayinn.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaymentServiceImpl implements PaymentService {
    
    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final BookingService bookingService;
    
    @Override
    public PaymentResponseDTO createPayment(PaymentCreateDTO paymentCreateDTO) {
        log.info("Creating payment for booking ID: {}", paymentCreateDTO.getBookingId());
        
        // Check if booking exists
        Booking booking = bookingRepository.findById(paymentCreateDTO.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + paymentCreateDTO.getBookingId()));
        
        // Check if payment already exists for this booking
        if (paymentRepository.existsByBookingId(paymentCreateDTO.getBookingId())) {
            throw new RuntimeException("Payment already exists for this booking");
        }
        
        // Verify amount matches booking total
        if (!paymentCreateDTO.getAmount().equals(booking.getTotalPrice())) {
            throw new RuntimeException("Payment amount does not match booking total");
        }
        
        // Create payment
        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setAmount(paymentCreateDTO.getAmount());
        payment.setPaymentMethod(paymentCreateDTO.getPaymentMethod());
        payment.setPaymentGateway(paymentCreateDTO.getPaymentGateway());
        payment.setPaymentDate(LocalDate.now());
        payment.setStatus(PaymentStatus.PENDING);
        
        // Generate temporary transaction ID (will be replaced by actual gateway ID)
        payment.setTransactionId("TXN_" + UUID.randomUUID().toString());
        
        Payment savedPayment = paymentRepository.save(payment);
        log.info("Payment created successfully with ID: {}", savedPayment.getId());
        
        return mapToResponseDTO(savedPayment);
    }
    
    @Override
    @Transactional(readOnly = true)
    public PaymentResponseDTO getPaymentById(Long id) {
        log.info("Fetching payment with ID: {}", id);
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found with ID: " + id));
        return mapToResponseDTO(payment);
    }
    
    @Override
    @Transactional(readOnly = true)
    public PaymentDetailDTO getPaymentDetailById(Long id) {
        log.info("Fetching detailed payment with ID: {}", id);
        Payment payment = paymentRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("Payment not found with ID: " + id));
        return mapToDetailDTO(payment);
    }
    
    @Override
    @Transactional(readOnly = true)
    public PaymentResponseDTO getPaymentByBookingId(Long bookingId) {
        log.info("Fetching payment for booking ID: {}", bookingId);
        Payment payment = paymentRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new RuntimeException("Payment not found for booking ID: " + bookingId));
        return mapToResponseDTO(payment);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDTO> getPaymentsByUserId(Long userId) {
        log.info("Fetching payments for user ID: {}", userId);
        return paymentRepository.findByUserId(userId).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SimplePaymentDTO> getSimplePaymentsByUserId(Long userId) {
        log.info("Fetching simple payments for user ID: {}", userId);
        return paymentRepository.findByUserId(userId).stream()
                .map(this::mapToSimpleDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDTO> getPaymentsByVillaId(Long villaId) {
        log.info("Fetching payments for villa ID: {}", villaId);
        return paymentRepository.findByVillaId(villaId).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public PaymentResponseDTO updatePaymentStatus(Long id, PaymentUpdateStatusDTO updateStatusDTO) {
        log.info("Updating payment {} status to {}", id, updateStatusDTO.getStatus());
        
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found with ID: " + id));
        
        payment.setStatus(updateStatusDTO.getStatus());
        
        if (updateStatusDTO.getTransactionId() != null) {
            payment.setTransactionId(updateStatusDTO.getTransactionId());
        }
        
        Payment updatedPayment = paymentRepository.save(payment);
        log.info("Payment status updated successfully");
        
        return mapToResponseDTO(updatedPayment);
    }
    
    @Override
    public PaymentResponseDTO completePayment(Long id, String transactionId) {
        log.info("Completing payment ID: {} with transaction ID: {}", id, transactionId);
        
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found with ID: " + id));
        
        if (payment.getStatus() == PaymentStatus.COMPLETED) {
            throw new RuntimeException("Payment is already completed");
        }
        
        // Update payment status
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setTransactionId(transactionId);
        payment.setPaymentDate(LocalDate.now());
        
        Payment completedPayment = paymentRepository.save(payment);
        
        // Confirm the booking
        bookingService.confirmBooking(payment.getBooking().getId());
        
        log.info("Payment completed and booking confirmed");
        return mapToResponseDTO(completedPayment);
    }
    
    @Override
    public PaymentResponseDTO failPayment(Long id, String reason) {
        log.info("Marking payment {} as failed. Reason: {}", id, reason);
        
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found with ID: " + id));
        
        payment.setStatus(PaymentStatus.FAILED);
        
        Payment failedPayment = paymentRepository.save(payment);
        log.info("Payment marked as failed");
        
        return mapToResponseDTO(failedPayment);
    }
    
    @Override
    public PaymentResponseDTO processRefund(RefundRequestDTO refundRequestDTO) {
        log.info("Processing refund for payment ID: {}", refundRequestDTO.getPaymentId());
        
        Payment payment = paymentRepository.findById(refundRequestDTO.getPaymentId())
                .orElseThrow(() -> new RuntimeException("Payment not found with ID: " + refundRequestDTO.getPaymentId()));
        
        // Validate payment can be refunded
        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new RuntimeException("Only completed payments can be refunded");
        }
        
        if (refundRequestDTO.getRefundAmount() > payment.getAmount()) {
            throw new RuntimeException("Refund amount cannot exceed payment amount");
        }
        
        // Update payment status
        payment.setStatus(PaymentStatus.REFUNDED);
        
        Payment refundedPayment = paymentRepository.save(payment);
        
        // Cancel the booking
        bookingService.cancelBooking(payment.getBooking().getId(), refundRequestDTO.getReason());
        
        log.info("Refund processed successfully. Amount: {}", refundRequestDTO.getRefundAmount());
        return mapToResponseDTO(refundedPayment);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean verifyPayment(PaymentVerificationDTO verificationDTO) {
        log.info("Verifying payment with transaction ID: {}", verificationDTO.getTransactionId());
        
        // This is a placeholder - implement actual gateway verification
        // Each payment gateway (Stripe, Razorpay, etc.) has its own verification API
        
        Payment payment = paymentRepository.findByTransactionId(verificationDTO.getTransactionId())
                .orElse(null);
        
        if (payment == null) {
            log.warn("Payment not found for transaction ID: {}", verificationDTO.getTransactionId());
            return false;
        }
        
        // Implement signature verification based on payment gateway
        // Example for Razorpay:
        // String generatedSignature = generateSignature(payment.getId(), payment.getTransactionId());
        // return generatedSignature.equals(verificationDTO.getSignature());
        
        log.info("Payment verification successful");
        return payment.getStatus() == PaymentStatus.COMPLETED;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDTO> getPaymentsByStatus(PaymentStatus status) {
        log.info("Fetching payments with status: {}", status);
        return paymentRepository.findByStatus(status).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDTO> getPaymentsByPaymentMethod(String paymentMethod) {
        log.info("Fetching payments with method: {}", paymentMethod);
        return paymentRepository.findByPaymentMethod(paymentMethod).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDTO> getSuccessfulPayments() {
        log.info("Fetching successful payments");
        return paymentRepository.findSuccessfulPayments().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDTO> getFailedPayments() {
        log.info("Fetching failed payments");
        return paymentRepository.findFailedPayments().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDTO> getRecentPayments(int days) {
        log.info("Fetching payments from last {} days", days);
        LocalDate cutoffDate = LocalDate.now().minusDays(days);
        return paymentRepository.findRecentPayments(cutoffDate).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }
    
//    @Override
//    @Transactional(readOnly = true)
//    public PaymentStatisticsDTO getPaymentStatistics() {
//        log.info("Generating payment statistics");
//        
//        List<Payment> allPayments = paymentRepository.findAll();
//        
//        long totalPayments = allPayments.size();
//        long completedPayments = paymentRepository.countByStatus(PaymentStatus.COMPLETED);
//        long pendingPayments = paymentRepository.countByStatus(PaymentStatus.PENDING);
//        long failedPayments = paymentRepository.countByStatus(PaymentStatus.FAILED);
//        long refundedPayments = paymentRepository.countByStatus(PaymentStatus.REFUNDED);
//        
//        Double totalRevenue = paymentRepository.calculateTotalRevenue();
//        if (totalRevenue == null) totalRevenue = 0.0;
//        
//        Double averagePaymentAmount = completedPayments > 0 ? totalRevenue / completedPayments : 0.0;
//        
//        // Get payment method distribution
//        List<Object[]> distributionData = paymentRepository.getPaymentMethodDistribution();
//        Map<String, Long> paymentMethodDistribution = new HashMap<>();
//        for (Object[] row : distributionData) {
//            paymentMethodDistribution.put((String) row[0], (Long) row[1]);
//        }
//        
//        return new PaymentStatisticsDTO(
//                totalPayments,
//                completedPayments,
//                pendingPayments,
//                failedPayments,
//                refundedPayments,
//                totalRevenue,
//                averagePaymentAmount,
//                paymentMethodDistribution
//        );
//    }
//    
//    @Override
//    @Transactional(readOnly = true)
//    public RevenueReportDTO getRevenueReport(LocalDate startDate, LocalDate endDate) {
//        log.info("Generating revenue report from {} to {}", startDate, endDate);
//        
//        Double totalRevenue = paymentRepository.calculateRevenueInDateRange(startDate, endDate);
//        if (totalRevenue == null) totalRevenue = 0.0;
//        
//        List<Payment> paymentsInRange = paymentRepository.findByPaymentDateBetween(startDate, endDate)
//                .stream()
//                .filter(p -> p.getStatus() == PaymentStatus.COMPLETED)
//                .collect(Collectors.toList());
//        
//        long totalTransactions = paymentsInRange.size();
//        Double averageTransactionValue = totalTransactions > 0 ? totalRevenue / totalTransactions : 0.0;
//        
//        // Revenue by payment method
//        Map<String, Double> revenueByPaymentMethod = paymentsInRange.stream()
//                .collect(Collectors.groupingBy(
//                        Payment::getPaymentMethod,
//                        Collectors.summingDouble(Payment::getAmount)
//                ));
//        
//        return new RevenueReportDTO(
//                startDate,
//                endDate,
//                totalRevenue,
//                totalTransactions,
//                averageTransactionValue,
//                revenueByPaymentMethod
//        );
//    }
    
    @Override
    @Transactional(readOnly = true)
    public Double calculateTotalRevenue() {
        Double revenue = paymentRepository.calculateTotalRevenue();
        return revenue != null ? revenue : 0.0;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDTO> getAllPayments() {
        log.info("Fetching all payments");
        return paymentRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public void deletePayment(Long id) {
        log.info("Deleting payment with ID: {}", id);
        
        if (!paymentRepository.existsById(id)) {
            throw new RuntimeException("Payment not found with ID: " + id);
        }
        
        paymentRepository.deleteById(id);
        log.info("Payment deleted successfully");
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getTotalPaymentCount() {
        return paymentRepository.count();
    }
    
    // ========== HELPER METHODS ==========
    
    private PaymentResponseDTO mapToResponseDTO(Payment payment) {
        return new PaymentResponseDTO(
                payment.getId(),
                payment.getBooking().getId(),
                payment.getAmount(),
                payment.getPaymentDate(),
                payment.getPaymentMethod(),
                payment.getPaymentGateway(),
                payment.getStatus(),
                payment.getTransactionId(),
                payment.getCreatedAt(),
                payment.getUpdatedAt()
        );
    }
    
    private PaymentDetailDTO mapToDetailDTO(Payment payment) {
        Booking booking = payment.getBooking();
        long numberOfNights = ChronoUnit.DAYS.between(
                booking.getCheckInDate(), 
                booking.getCheckOutDate()
        );
        
        PaymentDetailDTO dto = new PaymentDetailDTO();
        dto.setId(payment.getId());
        dto.setAmount(payment.getAmount());
        dto.setPaymentDate(payment.getPaymentDate());
        dto.setPaymentMethod(payment.getPaymentMethod());
        dto.setPaymentGateway(payment.getPaymentGateway());
        dto.setStatus(payment.getStatus());
        dto.setTransactionId(payment.getTransactionId());
        dto.setBookingId(booking.getId());
        dto.setCheckInDate(booking.getCheckInDate());
        dto.setCheckOutDate(booking.getCheckOutDate());
        dto.setNumberOfNights((int) numberOfNights);
        dto.setUserId(booking.getUser().getId());
        dto.setUserName(booking.getUser().getName());
        dto.setUserEmail(booking.getUser().getEmail());
        dto.setVillaId(booking.getVilla().getId());
        dto.setVillaName(booking.getVilla().getName());
        dto.setVillaAddress(booking.getVilla().getAddress());
        dto.setCreatedAt(payment.getCreatedAt());
        dto.setUpdatedAt(payment.getUpdatedAt());
        
        return dto;
    }
    
    private SimplePaymentDTO mapToSimpleDTO(Payment payment) {
        return new SimplePaymentDTO(
                payment.getId(),
                payment.getAmount(),
                payment.getPaymentDate(),
                payment.getPaymentMethod(),
                payment.getStatus(),
                payment.getBooking().getVilla().getName()
        );
    }
}