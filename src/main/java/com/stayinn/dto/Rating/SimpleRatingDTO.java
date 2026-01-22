package com.stayinn.dto.Rating;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleRatingDTO {
    
    private Long id;
    private String userName;
    private Integer score;
    private String feedback;
    private LocalDate ratingDate;
}
