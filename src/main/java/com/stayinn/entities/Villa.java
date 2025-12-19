package com.stayinn.entities;

import java.util.List;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@RequiredArgsConstructor
@AttributeOverride(name = "id",column = @Column(name = "villa_id"))
@Getter
@Setter
@Table(name = "villas") // Explicit table name
public class Villa extends BaseEntity{

    
    private String name;
    private String description;
    private String address;
    private Double pricePerNight;
    
    @ElementCollection
    private List<String> imageUrls; // Store URLs, not actual images in DB

    @OneToMany(mappedBy = "villa")
    private List<Rating> ratings; // All ratings for this villa

    // Helper method to calculate average rating
    public Double getAverageRating() {
        if (ratings == null || ratings.isEmpty()) return 0.0;
        return ratings.stream().mapToInt(Rating::getScore).average().orElse(0.0);
    }
}
