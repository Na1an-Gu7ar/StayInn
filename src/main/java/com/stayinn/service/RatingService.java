package com.stayinn.service;

import java.util.List;

import com.stayinn.dto.Rating.RatingCreateDTO;
import com.stayinn.dto.Rating.RatingDetailDTO;
import com.stayinn.dto.Rating.RatingResponseDTO;
import com.stayinn.dto.Rating.RatingUpdateDTO;
import com.stayinn.dto.Rating.SimpleRatingDTO;

public interface RatingService {
    
    /**
     * Create a new rating
     * @param ratingCreateDTO rating details
     * @return created rating
     * @throws RuntimeException if user has already rated this villa or hasn't completed booking
     */
    RatingResponseDTO createRating(RatingCreateDTO ratingCreateDTO);
    
    /**
     * Get rating by ID
     * @param id rating ID
     * @return rating details
     * @throws RuntimeException if rating not found
     */
    RatingResponseDTO getRatingById(Long id);
    
    /**
     * Get detailed rating information
     * @param id rating ID
     * @return detailed rating with user and villa info
     * @throws RuntimeException if rating not found
     */
    RatingDetailDTO getRatingDetailById(Long id);
    
    /**
     * Get all ratings by user ID
     * @param userId user ID
     * @return list of user's ratings
     */
    List<RatingResponseDTO> getRatingsByUserId(Long userId);
    
    /**
     * Get all ratings by villa ID
     * @param villaId villa ID
     * @return list of villa's ratings (most recent first)
     */
    List<RatingResponseDTO> getRatingsByVillaId(Long villaId);
    
    /**
     * Get simple ratings for villa (for display on villa page)
     * @param villaId villa ID
     * @return list of simple rating DTOs
     */
    List<SimpleRatingDTO> getSimpleRatingsByVillaId(Long villaId);
    
    /**
     * Update rating
     * @param id rating ID
     * @param ratingUpdateDTO updated rating details
     * @return updated rating
     * @throws RuntimeException if rating not found
     */
    RatingResponseDTO updateRating(Long id, RatingUpdateDTO ratingUpdateDTO);
    
    /**
     * Delete rating
     * @param id rating ID
     * @throws RuntimeException if rating not found
     */
    void deleteRating(Long id);
    
    /**
     * Check if user has rated a villa
     * @param userId user ID
     * @param villaId villa ID
     * @return true if user has rated, false otherwise
     */
    boolean hasUserRatedVilla(Long userId, Long villaId);
    
    /**
     * Get user's rating for a specific villa
     * @param userId user ID
     * @param villaId villa ID
     * @return rating if exists
     * @throws RuntimeException if rating not found
     */
    RatingResponseDTO getUserRatingForVilla(Long userId, Long villaId);
    
    /**
     * Get rating summary for a villa
     * @param villaId villa ID
     * @return rating summary with distribution
     */
//    RatingSummaryDTO getVillaRatingSummary(Long villaId);
    
    /**
     * Get user rating summary
     * @param userId user ID
     * @return user's rating statistics
     */
//    UserRatingSummaryDTO getUserRatingSummary(Long userId);
    
    /**
     * Get overall rating statistics (Admin only)
     * @return overall rating statistics
     */
//    RatingStatisticsDTO getRatingStatistics();
    
    /**
     * Get ratings by score
     * @param score rating score (1-5)
     * @return list of ratings with specified score
     */
    List<RatingResponseDTO> getRatingsByScore(Integer score);
    
    /**
     * Get recent ratings (last N days)
     * @param days number of days
     * @return list of recent ratings
     */
    List<RatingResponseDTO> getRecentRatings(int days);
    
    /**
     * Get all ratings (Admin only)
     * @return list of all ratings
     */
    List<RatingResponseDTO> getAllRatings();
    
    /**
     * Calculate average rating for a villa
     * @param villaId villa ID
     * @return average rating
     */
    Double calculateAverageRating(Long villaId);
    
    /**
     * Get total rating count
     * @return total number of ratings
     */
    long getTotalRatingCount();
    
    /**
     * Get rating count by villa
     * @param villaId villa ID
     * @return number of ratings for villa
     */
    long getRatingCountByVilla(Long villaId);
}