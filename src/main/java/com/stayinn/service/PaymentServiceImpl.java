package com.stayinn.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.razorpay.Order;
import com.razorpay.Payment;
import com.razorpay.RazorpayClient;
import com.razorpay.Refund;
import com.stayinn.config.RazorpayConfig;
import com.stayinn.dto.Payment.PaymentCreateDTO;
import com.stayinn.dto.Payment.PaymentDetailDTO;
import com.stayinn.dto.Payment.PaymentResponseDTO;
import com.stayinn.dto.Payment.PaymentUpdateStatusDTO;
import com.stayinn.dto.Payment.PaymentVerificationDTO;
import com.stayinn.dto.Payment.RazorpayOrderResponse;
import com.stayinn.dto.Payment.RazorpayVerificationDTO;
import com.stayinn.dto.Payment.RefundRequestDTO;
import com.stayinn.dto.Payment.SimplePaymentDTO;
import com.stayinn.entities.Booking;
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
    private final RazorpayClient razorpayClient;
    private final RazorpayConfig razorpayConfig;
    
    @Override
    public PaymentResponseDTO createPayment(PaymentCreateDTO paymentCreateDTO) {
        log.info("Creating payment for booking ID: {}", paymentCreateDTO.getBookingId());
        
        Booking booking = bookingRepository.findById(paymentCreateDTO.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        
        if (paymentRepository.existsByBookingId(paymentCreateDTO.getBookingId())) {
            throw new RuntimeException("Payment already exists for this booking");
        }
        
        if (!paymentCreateDTO.getAmount().equals(booking.getTotalPrice())) {
            throw new RuntimeException("Payment amount does not match booking total");
        }
        
        com.stayinn.entities.Payment payment = new com.stayinn.entities.Payment();
        payment.setBooking(booking);
        payment.setAmount(paymentCreateDTO.getAmount());
        payment.setPaymentMethod(paymentCreateDTO.getPaymentMethod());
        payment.setPaymentGateway("RAZORPAY");
        payment.setPaymentDate(LocalDate.now());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setTransactionId("PENDING_" + UUID.randomUUID().toString().substring(0, 8));
        
        com.stayinn.entities.Payment savedPayment = paymentRepository.save(payment);
        log.info("Payment created with ID: {}", savedPayment.getId());
        
        return mapToResponseDTO(savedPayment);
    }
    
    // ========== RAZORPAY INTEGRATION ==========
    
    @Override
    public RazorpayOrderResponse createRazorpayOrder(Long bookingId) throws Exception {
        log.info("Creating Razorpay order for booking ID: {}", bookingId);
        
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        
        if (paymentRepository.existsByBookingId(bookingId)) {
            throw new RuntimeException("Payment already exists for this booking");
        }
        
        // Convert to paise
        Integer amountInPaise = (int) (booking.getTotalPrice() * 100);
        
        // Create payment record first
        com.stayinn.entities.Payment payment = new com.stayinn.entities.Payment();
        payment.setBooking(booking);
        payment.setAmount(booking.getTotalPrice());
        payment.setPaymentMethod("ONLINE");
        payment.setPaymentGateway("RAZORPAY");
        payment.setPaymentDate(LocalDate.now());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setTransactionId("PENDING_" + UUID.randomUUID().toString().substring(0, 8));
        
        com.stayinn.entities.Payment savedPayment = paymentRepository.save(payment);
        
        // Create Razorpay order
        String receipt = "receipt_" + bookingId + "_" + System.currentTimeMillis();
        
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amountInPaise);
        orderRequest.put("currency", razorpayConfig.getCurrency());
        orderRequest.put("receipt", receipt);
        
        JSONObject notes = new JSONObject();
        notes.put("booking_id", bookingId);
        notes.put("payment_id", savedPayment.getId());
        notes.put("user_id", booking.getUser().getId());
        notes.put("villa_id", booking.getVilla().getId());
        orderRequest.put("notes", notes);
        
        Order order = razorpayClient.orders.create(orderRequest);
        
        // Update payment with Razorpay order ID
        savedPayment.setTransactionId(order.get("id"));
        paymentRepository.save(savedPayment);
        
//        log.info("Razorpay order created: {}", order.get("id"));
        
        // Prepare response
        RazorpayOrderResponse response = new RazorpayOrderResponse();
        response.setOrderId(order.get("id"));
        response.setCurrency(order.get("currency"));
        response.setAmount(order.get("amount"));
        response.setKeyId(razorpayConfig.getKeyId());
        response.setBookingId(bookingId);
        response.setPaymentId(savedPayment.getId());
        response.setUserName(booking.getUser().getName());
        response.setUserEmail(booking.getUser().getEmail());
//        response.setUserPhone(booking.getUser().getMobile());
        response.setVillaName(booking.getVilla().getName());
        response.setCompanyName(razorpayConfig.getCompanyName());
        
        return response;
    }
    
    @Override
    public PaymentResponseDTO verifyAndCapturePayment(RazorpayVerificationDTO verificationDTO) throws Exception {
        log.info("Verifying Razorpay payment: {}", verificationDTO.getRazorpayPaymentId());
        
        // Verify signature
        if (!verifySignature(verificationDTO)) {
            throw new RuntimeException("Invalid payment signature");
        }
        
        // Fetch payment details from Razorpay
        Payment razorpayPayment = razorpayClient.payments.fetch(verificationDTO.getRazorpayPaymentId());
        
        String status = razorpayPayment.get("status");
        if (!"captured".equals(status) && !"authorized".equals(status)) {
            throw new RuntimeException("Payment not successful. Status: " + status);
        }
        
        // Find payment by order ID
        com.stayinn.entities.Payment payment = paymentRepository.findByTransactionId(verificationDTO.getRazorpayOrderId())
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        
        // Update payment
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setTransactionId(verificationDTO.getRazorpayPaymentId());
        payment.setPaymentDate(LocalDate.now());
        
        com.stayinn.entities.Payment completedPayment = paymentRepository.save(payment);
        
        // Confirm booking
        bookingService.confirmBooking(payment.getBooking().getId());
        
        log.info("Payment verified and booking confirmed");
        return mapToResponseDTO(completedPayment);
    }
    
    private boolean verifySignature(RazorpayVerificationDTO verification) {
        try {
            String payload = verification.getRazorpayOrderId() + "|" + verification.getRazorpayPaymentId();
            
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(
                razorpayConfig.getKeySecret().getBytes("UTF-8"), 
                "HmacSHA256"
            );
            sha256_HMAC.init(secretKey);
            
            byte[] hash = sha256_HMAC.doFinal(payload.getBytes("UTF-8"));
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            
            String generatedSignature = hexString.toString();
            boolean isValid = generatedSignature.equals(verification.getRazorpaySignature());
            
            log.info("Signature verification: {}", isValid);
            return isValid;
            
        } catch (Exception e) {
            log.error("Signature verification error: {}", e.getMessage());
            return false;
        }
    }
    
    // ========== EXISTING METHODS ==========
    
    @Override
    @Transactional(readOnly = true)
    public PaymentResponseDTO getPaymentById(Long id) {
        com.stayinn.entities.Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        return mapToResponseDTO(payment);
    }
    
    @Override
    @Transactional(readOnly = true)
    public PaymentDetailDTO getPaymentDetailById(Long id) {
        com.stayinn.entities.Payment payment = paymentRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        return mapToDetailDTO(payment);
    }
    
    @Override
    @Transactional(readOnly = true)
    public PaymentResponseDTO getPaymentByBookingId(Long bookingId) {
        com.stayinn.entities.Payment payment = paymentRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new RuntimeException("Payment not found for booking"));
        return mapToResponseDTO(payment);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDTO> getPaymentsByUserId(Long userId) {
        return paymentRepository.findByUserId(userId).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SimplePaymentDTO> getSimplePaymentsByUserId(Long userId) {
        return paymentRepository.findByUserId(userId).stream()
                .map(this::mapToSimpleDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDTO> getPaymentsByVillaId(Long villaId) {
        return paymentRepository.findByVillaId(villaId).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public PaymentResponseDTO updatePaymentStatus(Long id, PaymentUpdateStatusDTO updateStatusDTO) {
        com.stayinn.entities.Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        
        payment.setStatus(updateStatusDTO.getStatus());
        if (updateStatusDTO.getTransactionId() != null) {
            payment.setTransactionId(updateStatusDTO.getTransactionId());
        }
        
        com.stayinn.entities.Payment updatedPayment = paymentRepository.save(payment);
        return mapToResponseDTO(updatedPayment);
    }
    
    @Override
    public PaymentResponseDTO completePayment(Long id, String transactionId) {
        com.stayinn.entities.Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        
        if (payment.getStatus() == PaymentStatus.COMPLETED) {
            throw new RuntimeException("Payment already completed");
        }
        
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setTransactionId(transactionId);
        payment.setPaymentDate(LocalDate.now());
        
        com.stayinn.entities.Payment completedPayment = paymentRepository.save(payment);
        bookingService.confirmBooking(payment.getBooking().getId());
        
        return mapToResponseDTO(completedPayment);
    }
    
    @Override
    public PaymentResponseDTO failPayment(Long id, String reason) {
        com.stayinn.entities.Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        
        payment.setStatus(PaymentStatus.FAILED);
        com.stayinn.entities.Payment failedPayment = paymentRepository.save(payment);
        
        return mapToResponseDTO(failedPayment);
    }
    
    @Override
    public PaymentResponseDTO processRefund(RefundRequestDTO refundRequestDTO) {
        log.info("Processing refund for payment ID: {}", refundRequestDTO.getPaymentId());
        
        com.stayinn.entities.Payment payment = paymentRepository.findById(refundRequestDTO.getPaymentId())
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        
        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new RuntimeException("Only completed payments can be refunded");
        }
        
        if (refundRequestDTO.getRefundAmount() > payment.getAmount()) {
            throw new RuntimeException("Refund amount exceeds payment amount");
        }
        
        // Process refund through Razorpay
        try {
            Integer amountInPaise = (int) (refundRequestDTO.getRefundAmount() * 100);
            
            JSONObject refundRequest = new JSONObject();
            refundRequest.put("amount", amountInPaise);
            refundRequest.put("speed", "normal");
            
            JSONObject notes = new JSONObject();
            notes.put("reason", refundRequestDTO.getReason());
            notes.put("booking_id", payment.getBooking().getId());
            refundRequest.put("notes", notes);
            
            Refund refund = razorpayClient.payments.refund(payment.getTransactionId(), refundRequest);
//            log.info("Razorpay refund processed: {}", refund.get("id"));
            
        } catch (Exception e) {
            log.error("Razorpay refund failed: {}", e.getMessage());
            throw new RuntimeException("Refund processing failed: " + e.getMessage());
        }
        
        // Update payment status
        payment.setStatus(PaymentStatus.REFUNDED);
        com.stayinn.entities.Payment refundedPayment = paymentRepository.save(payment);
        
        // Cancel booking
        bookingService.cancelBooking(payment.getBooking().getId(), refundRequestDTO.getReason());
        
        return mapToResponseDTO(refundedPayment);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean verifyPayment(PaymentVerificationDTO verificationDTO) {
        com.stayinn.entities.Payment payment = paymentRepository.findByTransactionId(verificationDTO.getTransactionId())
                .orElse(null);
        return payment != null && payment.getStatus() == PaymentStatus.COMPLETED;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDTO> getPaymentsByStatus(PaymentStatus status) {
        return paymentRepository.findByStatus(status).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDTO> getPaymentsByPaymentMethod(String paymentMethod) {
        return paymentRepository.findByPaymentMethod(paymentMethod).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDTO> getSuccessfulPayments() {
        return paymentRepository.findSuccessfulPayments().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDTO> getFailedPayments() {
        return paymentRepository.findFailedPayments().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDTO> getRecentPayments(int days) {
        LocalDate cutoffDate = LocalDate.now().minusDays(days);
        return paymentRepository.findRecentPayments(cutoffDate).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }
    
//    @Override
//    @Transactional(readOnly = true)
//    public PaymentStatisticsDTO getPaymentStatistics() {
//        List<com.stayinn.entities.Payment> allPayments = paymentRepository.findAll();
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
//        List<Object[]> distributionData = paymentRepository.getPaymentMethodDistribution();
//        Map<String, Long> paymentMethodDistribution = new HashMap<>();
//        for (Object[] row : distributionData) {
//            paymentMethodDistribution.put((String) row[0], (Long) row[1]);
//        }
//        
//        return new PaymentStatisticsDTO(
//                totalPayments, completedPayments, pendingPayments, failedPayments,
//                refundedPayments, totalRevenue, averagePaymentAmount, paymentMethodDistribution
//        );
//    }
    
//    @Override
//    @Transactional(readOnly = true)
//    public RevenueReportDTO getRevenueReport(LocalDate startDate, LocalDate endDate) {
//        Double totalRevenue = paymentRepository.calculateRevenueInDateRange(startDate, endDate);
//        if (totalRevenue == null) totalRevenue = 0.0;
//        
//        List<com.stayinn.entities.Payment> paymentsInRange = paymentRepository
//                .findByPaymentDateBetween(startDate, endDate).stream()
//                .filter(p -> p.getStatus() == PaymentStatus.COMPLETED)
//                .collect(Collectors.toList());
//        
//        long totalTransactions = paymentsInRange.size();
//        Double averageTransactionValue = totalTransactions > 0 ? totalRevenue / totalTransactions : 0.0;
//        
//        Map<String, Double> revenueByPaymentMethod = paymentsInRange.stream()
//                .collect(Collectors.groupingBy(
//                        com.stayinn.entities.Payment::getPaymentMethod,
//                        Collectors.summingDouble(com.stayinn.entities.Payment::getAmount)
//                ));
//        
//        return new RevenueReportDTO(startDate, endDate, totalRevenue, totalTransactions,
//                averageTransactionValue, revenueByPaymentMethod);
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
        return paymentRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public void deletePayment(Long id) {
        if (!paymentRepository.existsById(id)) {
            throw new RuntimeException("Payment not found");
        }
        paymentRepository.deleteById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getTotalPaymentCount() {
        return paymentRepository.count();
    }
    
    // ========== HELPER METHODS ==========
    
    private PaymentResponseDTO mapToResponseDTO(com.stayinn.entities.Payment payment) {
        return new PaymentResponseDTO(
                payment.getId(), payment.getBooking().getId(), payment.getAmount(),
                payment.getPaymentDate(), payment.getPaymentMethod(), payment.getPaymentGateway(),
                payment.getStatus(), payment.getTransactionId(),
                payment.getCreatedAt(), payment.getUpdatedAt()
        );
    }
    
    private PaymentDetailDTO mapToDetailDTO(com.stayinn.entities.Payment payment) {
        Booking booking = payment.getBooking();
        long numberOfNights = ChronoUnit.DAYS.between(booking.getCheckInDate(), booking.getCheckOutDate());
        
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
    
    private SimplePaymentDTO mapToSimpleDTO(com.stayinn.entities.Payment payment) {
        return new SimplePaymentDTO(
                payment.getId(), payment.getAmount(), payment.getPaymentDate(),
                payment.getPaymentMethod(), payment.getStatus(),
                payment.getBooking().getVilla().getName()
        );
    }
}