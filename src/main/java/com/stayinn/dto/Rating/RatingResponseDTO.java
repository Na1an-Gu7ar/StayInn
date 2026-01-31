package com.stayinn.dto.Rating;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatingResponseDTO {
    
    private Long id;
    private Long userId;
    private String userName;
    private Long villaId;
    private String villaName;
    private Integer score;
    private String feedback;
    private String ratingDate;
    private String createdAt;
    private String updatedAt;
    
    // Simplified constructor without timestamps
    public RatingResponseDTO(Long id, Long userId, String userName, Long villaId, 
                            String villaName, Integer score, String feedback, String ratingDate) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.villaId = villaId;
        this.villaName = villaName;
        this.score = score;
        this.feedback = feedback;
        this.ratingDate = ratingDate;
    }
}
