package com.stayinn.dto.Rating;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatingDetailDTO {
    
    private Long id;
    
    // User details
    private Long userId;
    private String userName;
    private String userEmail;
    
    // Villa details
    private Long villaId;
    private String villaName;
    private String villaAddress;
    
    // Rating details
    private Integer score;
    private String feedback;
    private LocalDate ratingDate;
    
    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
