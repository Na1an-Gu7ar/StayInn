package com.stayinn.repository;

  

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.stayinn.entities.Rating;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    
    /**
     * Find all ratings by user ID
     */
    List<Rating> findByUserId(Long userId);
    
    /**
     * Find all ratings by villa ID
     */
    List<Rating> findByVillaId(Long villaId);
    
    /**
     * Find ratings by villa ID ordered by date (most recent first)
     */
    List<Rating> findByVillaIdOrderByRatingDateDesc(Long villaId);
    
    /**
     * Find ratings by user ID ordered by date (most recent first)
     */
    List<Rating> findByUserIdOrderByRatingDateDesc(Long userId);
    
    /**
     * Find rating by user ID and villa ID
     * Used to check if user has already rated a villa
     */
    Optional<Rating> findByUserIdAndVillaId(Long userId, Long villaId);
    
    /**
     * Check if user has already rated a villa
     */
    boolean existsByUserIdAndVillaId(Long userId, Long villaId);
    
    /**
     * Find ratings by score
     */
    List<Rating> findByScore(Integer score);
    
    /**
     * Find ratings by villa ID and score
     */
    List<Rating> findByVillaIdAndScore(Long villaId, Integer score);
    
    /**
     * Find rating with user and villa details (eager loading)
     */
    @Query("SELECT r FROM Rating r " +
           "LEFT JOIN FETCH r.user " +
           "LEFT JOIN FETCH r.villa " +
           "WHERE r.id = :ratingId")
    Optional<Rating> findByIdWithDetails(@Param("ratingId") Long ratingId);
    
    /**
     * Find ratings in a date range
     */
    List<Rating> findByRatingDateBetween(LocalDate startDate, LocalDate endDate);
    
    /**
     * Find ratings by villa in a date range
     */
    @Query("SELECT r FROM Rating r WHERE r.villa.id = :villaId " +
           "AND r.ratingDate BETWEEN :startDate AND :endDate " +
           "ORDER BY r.ratingDate DESC")
    List<Rating> findByVillaIdAndDateRange(
            @Param("villaId") Long villaId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
    
    /**
     * Calculate average rating for a villa
     */
    @Query("SELECT AVG(r.score) FROM Rating r WHERE r.villa.id = :villaId")
    Double calculateAverageRating(@Param("villaId") Long villaId);
    
    /**
     * Count ratings by villa
     */
    long countByVillaId(Long villaId);
    
    /**
     * Count ratings by user
     */
    long countByUserId(Long userId);
    
    /**
     * Count ratings by score
     */
    long countByScore(Integer score);
    
    /**
     * Get top-rated villas (with highest average ratings)
     */
    @Query("SELECT r.villa.id, AVG(r.score) as avgRating FROM Rating r " +
           "GROUP BY r.villa.id " +
           "ORDER BY avgRating DESC")
    List<Object[]> findTopRatedVillas();
    
    /**
     * Get rating distribution for a villa
     */
    @Query("SELECT r.score, COUNT(r) FROM Rating r WHERE r.villa.id = :villaId " +
           "GROUP BY r.score ORDER BY r.score DESC")
    List<Object[]> getRatingDistribution(@Param("villaId") Long villaId);
    
    /**
     * Find recent ratings (last N days)
     */
    @Query("SELECT r FROM Rating r WHERE r.ratingDate >= :date " +
           "ORDER BY r.ratingDate DESC")
    List<Rating> findRecentRatings(@Param("date") LocalDate date);
}
