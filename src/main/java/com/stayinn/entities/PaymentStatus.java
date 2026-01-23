package com.stayinn.entities;

/**
 * Payment Status Lifecycle:
 * PENDING -> COMPLETED
 *         -> FAILED
 *         -> REFUNDED
 */
public enum PaymentStatus {
    /**
     * Payment initiated but not yet processed
     */
    PENDING,
    
    /**
     * Payment successfully processed
     */
    COMPLETED,
    
    /**
     * Payment failed or was declined
     */
    FAILED,
    
    /**
     * Payment was refunded to the user
     */
    REFUNDED,
    
    /**
     * Payment is being processed by gateway
     */
    PROCESSING
}