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
import com.stayinn.service.PaymentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PaymentController {
    
    private final PaymentService paymentService;
    
    // ========== USER ENDPOINTS ==========
    
    /**
     * Create/Initiate a new payment
     * POST /api/payments
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createPayment(@Valid @RequestBody PaymentCreateDTO paymentCreateDTO) {
        try {
            PaymentResponseDTO payment = paymentService.createPayment(paymentCreateDTO);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Payment initiated successfully");
            response.put("data", payment);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    /**
     * Get payment by ID
     * GET /api/payments/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getPaymentById(@PathVariable Long id) {
        try {
            PaymentResponseDTO payment = paymentService.getPaymentById(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", payment);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
    
    /**
     * Get detailed payment information
     * GET /api/payments/{id}/details
     */
    @GetMapping("/{id}/details")
    public ResponseEntity<Map<String, Object>> getPaymentDetails(@PathVariable Long id) {
        try {
            PaymentDetailDTO paymentDetail = paymentService.getPaymentDetailById(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", paymentDetail);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
    
    /**
     * Get payment by booking ID
     * GET /api/payments/booking/{bookingId}
     */
    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<Map<String, Object>> getPaymentByBooking(@PathVariable Long bookingId) {
        try {
            PaymentResponseDTO payment = paymentService.getPaymentByBookingId(bookingId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", payment);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
    
    /**
     * Get all payments by user ID
     * GET /api/payments/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserPayments(@PathVariable Long userId) {
        List<PaymentResponseDTO> payments = paymentService.getPaymentsByUserId(userId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("count", payments.size());
        response.put("data", payments);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get simple payments for user (for display)
     * GET /api/payments/user/{userId}/simple
     */
    @GetMapping("/user/{userId}/simple")
    public ResponseEntity<Map<String, Object>> getSimpleUserPayments(@PathVariable Long userId) {
        List<SimplePaymentDTO> payments = paymentService.getSimplePaymentsByUserId(userId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("count", payments.size());
        response.put("data", payments);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Complete payment (webhook/callback endpoint)
     * POST /api/payments/{id}/complete
     */
    @PostMapping("/{id}/complete")
    public ResponseEntity<Map<String, Object>> completePayment(
            @PathVariable Long id,
            @RequestParam String transactionId) {
        try {
            PaymentResponseDTO payment = paymentService.completePayment(id, transactionId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Payment completed successfully");
            response.put("data", payment);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    /**
     * Verify payment with gateway
     * POST /api/payments/verify
     */
    @PostMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyPayment(@Valid @RequestBody PaymentVerificationDTO verificationDTO) {
        boolean isVerified = paymentService.verifyPayment(verificationDTO);
        Map<String, Object> response = new HashMap<>();
        response.put("success", isVerified);
        response.put("verified", isVerified);
        response.put("message", isVerified ? "Payment verified successfully" : "Payment verification failed");
        return ResponseEntity.ok(response);
    }
    
    /**
     * Request refund
     * POST /api/payments/refund
     */
    @PostMapping("/refund")
    public ResponseEntity<Map<String, Object>> requestRefund(@Valid @RequestBody RefundRequestDTO refundRequestDTO) {
        try {
            PaymentResponseDTO payment = paymentService.processRefund(refundRequestDTO);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Refund processed successfully");
            response.put("data", payment);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    /**
     * Update payment status
     * PATCH /api/payments/{id}/status
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> updatePaymentStatus(
            @PathVariable Long id,
            @Valid @RequestBody PaymentUpdateStatusDTO updateStatusDTO) {
        try {
            PaymentResponseDTO payment = paymentService.updatePaymentStatus(id, updateStatusDTO);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Payment status updated successfully");
            response.put("data", payment);
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
     * Get all payments (Admin only)
     * GET /api/payments
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllPayments() {
        List<PaymentResponseDTO> payments = paymentService.getAllPayments();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("count", payments.size());
        response.put("data", payments);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get payments by villa ID (Admin only)
     * GET /api/payments/villa/{villaId}
     */
    @GetMapping("/villa/{villaId}")
    public ResponseEntity<Map<String, Object>> getVillaPayments(@PathVariable Long villaId) {
        List<PaymentResponseDTO> payments = paymentService.getPaymentsByVillaId(villaId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("count", payments.size());
        response.put("data", payments);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get payments by status (Admin only)
     * GET /api/payments/status/{status}
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<Map<String, Object>> getPaymentsByStatus(@PathVariable PaymentStatus status) {
        List<PaymentResponseDTO> payments = paymentService.getPaymentsByStatus(status);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("count", payments.size());
        response.put("data", payments);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get payments by payment method (Admin only)
     * GET /api/payments/method/{method}
     */
    @GetMapping("/method/{method}")
    public ResponseEntity<Map<String, Object>> getPaymentsByMethod(@PathVariable String method) {
        List<PaymentResponseDTO> payments = paymentService.getPaymentsByPaymentMethod(method);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("count", payments.size());
        response.put("data", payments);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get successful payments (Admin only)
     * GET /api/payments/successful
     */
    @GetMapping("/successful")
    public ResponseEntity<Map<String, Object>> getSuccessfulPayments() {
        List<PaymentResponseDTO> payments = paymentService.getSuccessfulPayments();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("count", payments.size());
        response.put("data", payments);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get failed payments (Admin only)
     * GET /api/payments/failed
     */
    @GetMapping("/failed")
    public ResponseEntity<Map<String, Object>> getFailedPayments() {
        List<PaymentResponseDTO> payments = paymentService.getFailedPayments();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("count", payments.size());
        response.put("data", payments);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get recent payments (Admin only)
     * GET /api/payments/recent?days=7
     */
    @GetMapping("/recent")
    public ResponseEntity<Map<String, Object>> getRecentPayments(
            @RequestParam(defaultValue = "7") int days) {
        List<PaymentResponseDTO> payments = paymentService.getRecentPayments(days);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("count", payments.size());
        response.put("data", payments);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get payment statistics (Admin only)
     * GET /api/payments/statistics
     */
//    @GetMapping("/statistics")
//    public ResponseEntity<Map<String, Object>> getPaymentStatistics() {
//        PaymentStatisticsDTO statistics = paymentService.getPaymentStatistics();
//        Map<String, Object> response = new HashMap<>();
//        response.put("success", true);
//        response.put("data", statistics);
//        return ResponseEntity.ok(response);
//    }
    
    /**
     * Get revenue report (Admin only)
     * GET /api/payments/revenue-report?startDate=2024-01-01&endDate=2024-12-31
     */
//    @GetMapping("/revenue-report")
//    public ResponseEntity<Map<String, Object>> getRevenueReport(
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
//        RevenueReportDTO report = paymentService.getRevenueReport(startDate, endDate);
//        Map<String, Object> response = new HashMap<>();
//        response.put("success", true);
//        response.put("data", report);
//        return ResponseEntity.ok(response);
//    }
    
    /**
     * Get total revenue (Admin only)
     * GET /api/payments/total-revenue
     */
    @GetMapping("/total-revenue")
    public ResponseEntity<Map<String, Object>> getTotalRevenue() {
        Double totalRevenue = paymentService.calculateTotalRevenue();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("totalRevenue", totalRevenue);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Delete payment (Admin only)
     * DELETE /api/payments/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deletePayment(@PathVariable Long id) {
        try {
            paymentService.deletePayment(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Payment deleted successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
    
    // ========== RAZORPAY ENDPOINTS ==========
    
    /**
     * Create Razorpay order for booking
     * POST /api/payments/razorpay/create-order/{bookingId}
     */
    @PostMapping("/razorpay/create-order/{bookingId}")
    public ResponseEntity<Map<String, Object>> createRazorpayOrder(@PathVariable Long bookingId) {
        try {
            RazorpayOrderResponse orderResponse = paymentService.createRazorpayOrder(bookingId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Razorpay order created successfully");
            response.put("data", orderResponse);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to create order: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Verify Razorpay payment
     * POST /api/payments/razorpay/verify
     */
    @PostMapping("/razorpay/verify")
    public ResponseEntity<Map<String, Object>> verifyRazorpayPayment(
            @Valid @RequestBody RazorpayVerificationDTO verificationDTO) {
        try {
            PaymentResponseDTO payment = paymentService.verifyAndCapturePayment(verificationDTO);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Payment verified and completed successfully");
            response.put("data", payment);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Payment verification failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}