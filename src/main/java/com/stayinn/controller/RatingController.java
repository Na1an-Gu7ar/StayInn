package com.stayinn.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stayinn.dto.Rating.RatingCreateDTO;
import com.stayinn.dto.Rating.RatingDetailDTO;
import com.stayinn.dto.Rating.RatingResponseDTO;
import com.stayinn.dto.Rating.RatingUpdateDTO;
import com.stayinn.dto.Rating.SimpleRatingDTO;
import com.stayinn.service.RatingService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
public class RatingController {
    
    private final RatingService ratingService;
    
    // ========== USER ENDPOINTS ==========
    
    /**
     * Create a new rating
     * POST /api/ratings
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createRating(@Valid @RequestBody RatingCreateDTO ratingCreateDTO) {
        try {
            RatingResponseDTO rating = ratingService.createRating(ratingCreateDTO);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Rating submitted successfully");
            response.put("data", rating);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    /**
     * Get rating by ID
     * GET /api/ratings/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getRatingById(@PathVariable Long id) {
        try {
            RatingResponseDTO rating = ratingService.getRatingById(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", rating);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
    
    /**
     * Get detailed rating information
     * GET /api/ratings/{id}/details
     */
    @GetMapping("/{id}/details")
    public ResponseEntity<Map<String, Object>> getRatingDetails(@PathVariable Long id) {
        try {
            RatingDetailDTO ratingDetail = ratingService.getRatingDetailById(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", ratingDetail);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
    
    /**
     * Get all ratings by user ID
     * GET /api/ratings/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserRatings(@PathVariable Long userId) {
        List<RatingResponseDTO> ratings = ratingService.getRatingsByUserId(userId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("count", ratings.size());
        response.put("data", ratings);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get user rating summary
     * GET /api/ratings/user/{userId}/summary
     */
//    @GetMapping("/user/{userId}/summary")
//    public ResponseEntity<Map<String, Object>> getUserRatingSummary(@PathVariable Long userId) {
//        UserRatingSummaryDTO summary = ratingService.getUserRatingSummary(userId);
//        Map<String, Object> response = new HashMap<>();
//        response.put("success", true);
//        response.put("data", summary);
//        return ResponseEntity.ok(response);
//    }
    
    /**
     * Get all ratings for a villa
     * GET /api/ratings/villa/{villaId}
     */
    @GetMapping("/villa/{villaId}")
    public ResponseEntity<Map<String, Object>> getVillaRatings(@PathVariable Long villaId) {
        List<RatingResponseDTO> ratings = ratingService.getRatingsByVillaId(villaId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("count", ratings.size());
        response.put("data", ratings);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get simple ratings for villa (for display)
     * GET /api/ratings/villa/{villaId}/simple
     */
    @GetMapping("/villa/{villaId}/simple")
    public ResponseEntity<Map<String, Object>> getSimpleVillaRatings(@PathVariable Long villaId) {
        List<SimpleRatingDTO> ratings = ratingService.getSimpleRatingsByVillaId(villaId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("count", ratings.size());
        response.put("data", ratings);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get villa rating summary (with distribution)
     * GET /api/ratings/villa/{villaId}/summary
     */
//    @GetMapping("/villa/{villaId}/summary")
//    public ResponseEntity<Map<String, Object>> getVillaRatingSummary(@PathVariable Long villaId) {
//        RatingSummaryDTO summary = ratingService.getVillaRatingSummary(villaId);
//        Map<String, Object> response = new HashMap<>();
//        response.put("success", true);
//        response.put("data", summary);
//        return ResponseEntity.ok(response);
//    }
    
    /**
     * Check if user has rated a villa
     * GET /api/ratings/check?userId=1&villaId=1
     */
    @GetMapping("/check")
    public ResponseEntity<Map<String, Object>> checkUserRating(
            @RequestParam Long userId,
            @RequestParam Long villaId) {
        boolean hasRated = ratingService.hasUserRatedVilla(userId, villaId);
        Map<String, Object> response = new HashMap<>();
        response.put("hasRated", hasRated);
        response.put("message", hasRated ? "User has already rated this villa" : "User hasn't rated this villa yet");
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get user's rating for a specific villa
     * GET /api/ratings/user/{userId}/villa/{villaId}
     */
    @GetMapping("/user/{userId}/villa/{villaId}")
    public ResponseEntity<Map<String, Object>> getUserRatingForVilla(
            @PathVariable Long userId,
            @PathVariable Long villaId) {
        try {
            RatingResponseDTO rating = ratingService.getUserRatingForVilla(userId, villaId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", rating);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
    
    /**
     * Update rating
     * PUT /api/ratings/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateRating(
            @PathVariable Long id,
            @Valid @RequestBody RatingUpdateDTO ratingUpdateDTO) {
        try {
            RatingResponseDTO rating = ratingService.updateRating(id, ratingUpdateDTO);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Rating updated successfully");
            response.put("data", rating);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    /**
     * Delete rating
     * DELETE /api/ratings/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteRating(@PathVariable Long id) {
        try {
            ratingService.deleteRating(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Rating deleted successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
    
    // ========== ADMIN ENDPOINTS ==========
    
    /**
     * Get all ratings (Admin only)
     * GET /api/ratings
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllRatings() {
        List<RatingResponseDTO> ratings = ratingService.getAllRatings();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("count", ratings.size());
        response.put("data", ratings);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get ratings by score (Admin only)
     * GET /api/ratings/score/{score}
     */
    @GetMapping("/score/{score}")
    public ResponseEntity<Map<String, Object>> getRatingsByScore(@PathVariable Integer score) {
        if (score < 1 || score > 5) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Score must be between 1 and 5");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        
        List<RatingResponseDTO> ratings = ratingService.getRatingsByScore(score);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("count", ratings.size());
        response.put("data", ratings);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get recent ratings (Admin only)
     * GET /api/ratings/recent?days=7
     */
    @GetMapping("/recent")
    public ResponseEntity<Map<String, Object>> getRecentRatings(
            @RequestParam(defaultValue = "7") int days) {
        List<RatingResponseDTO> ratings = ratingService.getRecentRatings(days);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("count", ratings.size());
        response.put("data", ratings);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get overall rating statistics (Admin only)
     * GET /api/ratings/statistics
     */
//    @GetMapping("/statistics")
//    public ResponseEntity<Map<String, Object>> getRatingStatistics() {
//        RatingStatisticsDTO statistics = ratingService.getRatingStatistics();
//        Map<String, Object> response = new HashMap<>();
//        response.put("success", true);
//        response.put("data", statistics);
//        return ResponseEntity.ok(response);
//    }
}