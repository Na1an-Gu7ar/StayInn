package com.stayinn.entities;

import java.time.LocalDate;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@RequiredArgsConstructor
@AttributeOverride(name = "id",column = @Column(name = "user_id"))
@Getter
@Setter
@Table(name = "payments") // Explicit table name
public class Payment extends BaseEntity {

    private Double amount;
    private LocalDate paymentDate;
    private String paymentMethod; // e.g., "CREDIT_CARD", "PAYPAL"
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status = PaymentStatus.PENDING;

    private String transactionId; // Payment gateway transaction ID
    private String paymentGateway; // e.g., "STRIPE", "RAZORPAY", "PAYPAL"

    @OneToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;
}