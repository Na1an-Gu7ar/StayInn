package com.stayinn.service;

import java.util.List;

import com.stayinn.dto.Villa.VillaCreateDTO;
import com.stayinn.dto.Villa.VillaDetailDTO;
import com.stayinn.dto.Villa.VillaResponseDTO;
import com.stayinn.dto.Villa.VillaSearchDTO;
import com.stayinn.dto.Villa.VillaUpdateDTO;

public interface VillaService {
    
    /**
     * Create a new villa (Admin only)
     * @param villaCreateDTO villa details
     * @return created villa
     * @throws RuntimeException if villa name already exists
     */
    VillaResponseDTO createVilla(VillaCreateDTO villaCreateDTO);
    
    /**
     * Get villa by ID
     * @param id villa ID
     * @return villa details
     * @throws RuntimeException if villa not found
     */
    VillaResponseDTO getVillaById(Long id);
    
    /**
     * Get detailed villa information with ratings and bookings
     * @param id villa ID
     * @return detailed villa information
     * @throws RuntimeException if villa not found
     */
    VillaDetailDTO getVillaDetailById(Long id);
    
    /**
     * Get all villas
     * @return list of all villas
     */
    List<VillaResponseDTO> getAllVillas();
    
    /**
     * Get villa summaries for listing page
     * @return list of villa summaries
     */
//    List<VillaSummaryDTO> getAllVillaSummaries();
    
    /**
     * Update villa details (Admin only)
     * @param id villa ID
     * @param villaUpdateDTO updated details
     * @return updated villa
     * @throws RuntimeException if villa not found
     */
    VillaResponseDTO updateVilla(Long id, VillaUpdateDTO villaUpdateDTO);
    
    /**
     * Delete villa (Admin only)
     * @param id villa ID
     * @throws RuntimeException if villa not found
     */
    void deleteVilla(Long id);
    
    /**
     * Search villas by keyword (name or address)
     * @param keyword search keyword
     * @return list of matching villas
     */
    List<VillaResponseDTO> searchVillas(String keyword);
    
    /**
     * Filter villas by multiple criteria
     * @param searchDTO search and filter criteria
     * @return list of filtered villas
     */
    List<VillaResponseDTO> filterVillas(VillaSearchDTO searchDTO);
    
    /**
     * Get villas by price range
     * @param minPrice minimum price
     * @param maxPrice maximum price
     * @return list of villas within price range
     */
    List<VillaResponseDTO> getVillasByPriceRange(Double minPrice, Double maxPrice);
    
    /**
     * Get villas by location/address
     * @param address location keyword
     * @return list of villas matching location
     */
    List<VillaResponseDTO> getVillasByAddress(String address);
    
    /**
     * Get top-rated villas
     * @param limit number of villas to return
     * @return list of top-rated villas
     */
    List<VillaResponseDTO> getTopRatedVillas(int limit);
    
    /**
     * Get villas sorted by price
     * @param ascending true for low to high, false for high to low
     * @return sorted list of villas
     */
    List<VillaResponseDTO> getVillasSortedByPrice(boolean ascending);
    
    /**
     * Add image URL to villa
     * @param villaId villa ID
     * @param imageUrl image URL to add
     * @return updated villa
     * @throws RuntimeException if villa not found or max images reached
     */
    VillaResponseDTO addImageToVilla(Long villaId, String imageUrl);
    
    /**
     * Remove image URL from villa
     * @param villaId villa ID
     * @param imageUrl image URL to remove
     * @return updated villa
     * @throws RuntimeException if villa not found
     */
    VillaResponseDTO removeImageFromVilla(Long villaId, String imageUrl);
    
    /**
     * Get villa statistics for admin dashboard
     * @return statistics summary
     */
//    VillaStatisticsDTO getVillaStatistics();
    
    /**
     * Check if villa exists by name
     * @param name villa name
     * @return true if exists, false otherwise
     */
    boolean existsByName(String name);
    
    /**
     * Get total villa count
     * @return number of villas
     */
    long getTotalVillaCount();
}