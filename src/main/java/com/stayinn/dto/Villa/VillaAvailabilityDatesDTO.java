package com.stayinn.dto.Villa;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VillaAvailabilityDatesDTO {
    
    private Long villaId;
    private String villaName;
    private List<String> bookedDates; // List of booked date ranges
    private Boolean isCurrentlyAvailable;
}
