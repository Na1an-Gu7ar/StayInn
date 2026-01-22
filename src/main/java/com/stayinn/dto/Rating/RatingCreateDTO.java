package com.stayinn.dto.Rating;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// ========== RATING CREATE DTO ==========
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatingCreateDTO {
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    @NotNull(message = "Villa ID is required")
    private Long villaId;
    
    @NotNull(message = "Rating score is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private Integer score;
    
    @NotBlank(message = "Feedback is required")
    @Size(min = 10, max = 500, message = "Feedback must be between 10 and 500 characters")
    private String feedback;
}

