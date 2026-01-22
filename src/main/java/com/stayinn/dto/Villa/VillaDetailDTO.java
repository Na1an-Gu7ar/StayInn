package com.stayinn.dto.Villa;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VillaDetailDTO {
    
    private Long id;
    private String name;
    private String description;
    private String address;
    private Double pricePerNight;
    private List<String> imageUrls;
    
    // Rating Information
    private Double averageRating;
    private Integer totalRatings;
    private Integer fiveStarCount;
    private Integer fourStarCount;
    private Integer threeStarCount;
    private Integer twoStarCount;
    private Integer oneStarCount;
    
    // Booking Statistics
    private Long totalBookings;
    private Long confirmedBookings;
    private Boolean isAvailable; // General availability status
    
    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

