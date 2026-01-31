package com.stayinn.dto.Villa;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VillaResponseDTO {
    
    private Long id;
    private String name;
    private String description;
    private String address;
    private Double pricePerNight;
    private List<String> imageUrls;
    private Double averageRating;
    private Integer totalRatings;
    private String createdAt;
    private String updatedAt;
    
    // Simplified constructor without timestamps
    public VillaResponseDTO(Long id, String name, String description, String address, 
                           Double pricePerNight, List<String> imageUrls, 
                           Double averageRating, Integer totalRatings) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.address = address;
        this.pricePerNight = pricePerNight;
        this.imageUrls = imageUrls;
        this.averageRating = averageRating;
        this.totalRatings = totalRatings;
    }
}
