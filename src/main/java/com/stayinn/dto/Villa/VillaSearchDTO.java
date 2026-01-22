package com.stayinn.dto.Villa;

import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VillaSearchDTO {
    
    private String name;
    private String address;
    
    @DecimalMin(value = "0.0", message = "Minimum price must be 0 or greater")
    private Double minPrice;
    
    @DecimalMin(value = "0.0", message = "Maximum price must be 0 or greater")
    private Double maxPrice;
    
    private String sortBy; // "price_asc", "price_desc", "rating", "name"
}
