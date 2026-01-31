package com.stayinn.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stayinn.dto.Rating.RatingCreateDTO;
import com.stayinn.dto.Rating.RatingDetailDTO;
import com.stayinn.dto.Rating.RatingResponseDTO;
import com.stayinn.dto.Rating.RatingUpdateDTO;
import com.stayinn.dto.Rating.SimpleRatingDTO;
import com.stayinn.entities.Rating;
import com.stayinn.entities.User;
import com.stayinn.entities.Villa;
import com.stayinn.repository.BookingRepository;
import com.stayinn.repository.RatingRepository;
import com.stayinn.repository.UserRepository;
import com.stayinn.repository.VillaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RatingServiceImpl implements RatingService {
    
    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;
    private final VillaRepository villaRepository;
    private final BookingRepository bookingRepository;
    
    @Override
    public RatingResponseDTO createRating(RatingCreateDTO ratingCreateDTO) {
        log.info("Creating rating for villa {} by user {}", 
                ratingCreateDTO.getVillaId(), ratingCreateDTO.getUserId());
        
        // Check if user exists
        User user = userRepository.findById(ratingCreateDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + ratingCreateDTO.getUserId()));
        
        // Check if villa exists
        Villa villa = villaRepository.findById(ratingCreateDTO.getVillaId())
                .orElseThrow(() -> new RuntimeException("Villa not found with ID: " + ratingCreateDTO.getVillaId()));
        
        // Check if user has already rated this villa
        if (ratingRepository.existsByUserIdAndVillaId(ratingCreateDTO.getUserId(), ratingCreateDTO.getVillaId())) {
            throw new RuntimeException("You have already rated this villa. Please update your existing rating instead.");
        }
        
        // Verify user has completed a booking at this villa (business rule)
        boolean hasCompletedBooking = bookingRepository.hasUserBookedVilla(
                ratingCreateDTO.getUserId(), 
                ratingCreateDTO.getVillaId()
        );
        
        if (!hasCompletedBooking) {
            throw new RuntimeException("You can only rate villas where you have completed a booking");
        }
        
        // Create rating
        Rating rating = new Rating();
        rating.setUser(user);
        rating.setVilla(villa);
        rating.setScore(ratingCreateDTO.getScore());
        rating.setFeedback(ratingCreateDTO.getFeedback());
        rating.setRatingDate(LocalDate.now());
        
        Rating savedRating = ratingRepository.save(rating);
        log.info("Rating created successfully with ID: {}", savedRating.getId());
        
        return mapToResponseDTO(savedRating);
    }
    
    @Override
    @Transactional(readOnly = true)
    public RatingResponseDTO getRatingById(Long id) {
        log.info("Fetching rating with ID: {}", id);
        Rating rating = ratingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rating not found with ID: " + id));
        return mapToResponseDTO(rating);
    }
    
    @Override
    @Transactional(readOnly = true)
    public RatingDetailDTO getRatingDetailById(Long id) {
        log.info("Fetching detailed rating with ID: {}", id);
        Rating rating = ratingRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("Rating not found with ID: " + id));
        return mapToDetailDTO(rating);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<RatingResponseDTO> getRatingsByUserId(Long userId) {
        log.info("Fetching ratings for user ID: {}", userId);
        return ratingRepository.findByUserIdOrderByRatingDateDesc(userId).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<RatingResponseDTO> getRatingsByVillaId(Long villaId) {
        log.info("Fetching ratings for villa ID: {}", villaId);
        return ratingRepository.findByVillaIdOrderByRatingDateDesc(villaId).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SimpleRatingDTO> getSimpleRatingsByVillaId(Long villaId) {
        log.info("Fetching simple ratings for villa ID: {}", villaId);
        return ratingRepository.findByVillaIdOrderByRatingDateDesc(villaId).stream()
                .map(this::mapToSimpleDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public RatingResponseDTO updateRating(Long id, RatingUpdateDTO ratingUpdateDTO) {
        log.info("Updating rating with ID: {}", id);
        
        Rating rating = ratingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rating not found with ID: " + id));
        
        // Update fields if provided
        if (ratingUpdateDTO.getScore() != null) {
            rating.setScore(ratingUpdateDTO.getScore());
        }
        
        if (ratingUpdateDTO.getFeedback() != null) {
            rating.setFeedback(ratingUpdateDTO.getFeedback());
        }
        
        // Update rating date to current date when modified
        rating.setRatingDate(LocalDate.now());
        
        Rating updatedRating = ratingRepository.save(rating);
        log.info("Rating updated successfully");
        
        return mapToResponseDTO(updatedRating);
    }
    
    @Override
    public void deleteRating(Long id) {
        log.info("Deleting rating with ID: {}", id);
        
        if (!ratingRepository.existsById(id)) {
            throw new RuntimeException("Rating not found with ID: " + id);
        }
        
        ratingRepository.deleteById(id);
        log.info("Rating deleted successfully");
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean hasUserRatedVilla(Long userId, Long villaId) {
        return ratingRepository.existsByUserIdAndVillaId(userId, villaId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public RatingResponseDTO getUserRatingForVilla(Long userId, Long villaId) {
        log.info("Fetching rating for user {} and villa {}", userId, villaId);
        Rating rating = ratingRepository.findByUserIdAndVillaId(userId, villaId)
                .orElseThrow(() -> new RuntimeException("Rating not found for user and villa"));
        return mapToResponseDTO(rating);
    }
    
//    @Override
//    @Transactional(readOnly = true)
//    public RatingSummaryDTO getVillaRatingSummary(Long villaId) {
//        log.info("Generating rating summary for villa ID: {}", villaId);
//        
//        List<Rating> ratings = ratingRepository.findByVillaId(villaId);
//        
//        if (ratings.isEmpty()) {
//            return new RatingSummaryDTO(villaId, 0.0, 0L, 0L, 0L, 0L, 0L, 0L);
//        }
//        
//        Double averageRating = ratings.stream()
//                .mapToInt(Rating::getScore)
//                .average()
//                .orElse(0.0);
//        
//        long totalRatings = ratings.size();
//        long fiveStarCount = ratings.stream().filter(r -> r.getScore() == 5).count();
//        long fourStarCount = ratings.stream().filter(r -> r.getScore() == 4).count();
//        long threeStarCount = ratings.stream().filter(r -> r.getScore() == 3).count();
//        long twoStarCount = ratings.stream().filter(r -> r.getScore() == 2).count();
//        long oneStarCount = ratings.stream().filter(r -> r.getScore() == 1).count();
//        
//        return new RatingSummaryDTO(
//                villaId,
//                averageRating,
//                totalRatings,
//                fiveStarCount,
//                fourStarCount,
//                threeStarCount,
//                twoStarCount,
//                oneStarCount
//        );
//    }
//    
//    @Override
//    @Transactional(readOnly = true)
//    public UserRatingSummaryDTO getUserRatingSummary(Long userId) {
//        log.info("Generating rating summary for user ID: {}", userId);
//        
//        List<Rating> ratings = ratingRepository.findByUserId(userId);
//        
//        if (ratings.isEmpty()) {
//            return new UserRatingSummaryDTO(userId, 0L, 0.0, 0L, 0L, 0L, 0L, 0L);
//        }
//        
//        long totalRatings = ratings.size();
//        
//        Double averageRating = ratings.stream()
//                .mapToInt(Rating::getScore)
//                .average()
//                .orElse(0.0);
//        
//        long fiveStarRatings = ratings.stream().filter(r -> r.getScore() == 5).count();
//        long fourStarRatings = ratings.stream().filter(r -> r.getScore() == 4).count();
//        long threeStarRatings = ratings.stream().filter(r -> r.getScore() == 3).count();
//        long twoStarRatings = ratings.stream().filter(r -> r.getScore() == 2).count();
//        long oneStarRatings = ratings.stream().filter(r -> r.getScore() == 1).count();
//        
//        return new UserRatingSummaryDTO(
//                userId,
//                totalRatings,
//                averageRating,
//                fiveStarRatings,
//                fourStarRatings,
//                threeStarRatings,
//                twoStarRatings,
//                oneStarRatings
//        );
//    }
//    
//    @Override
//    @Transactional(readOnly = true)
//    public RatingStatisticsDTO getRatingStatistics() {
//        log.info("Generating overall rating statistics");
//        
//        List<Rating> allRatings = ratingRepository.findAll();
//        
//        if (allRatings.isEmpty()) {
//            return new RatingStatisticsDTO(0L, 0.0, 0L, 0L, 0L, 0L, 0L, new HashMap<>());
//        }
//        
//        long totalRatings = allRatings.size();
//        
//        Double overallAverage = allRatings.stream()
//                .mapToInt(Rating::getScore)
//                .average()
//                .orElse(0.0);
//        
//        long totalFiveStars = allRatings.stream().filter(r -> r.getScore() == 5).count();
//        long totalFourStars = allRatings.stream().filter(r -> r.getScore() == 4).count();
//        long totalThreeStars = allRatings.stream().filter(r -> r.getScore() == 3).count();
//        long totalTwoStars = allRatings.stream().filter(r -> r.getScore() == 2).count();
//        long totalOneStars = allRatings.stream().filter(r -> r.getScore() == 1).count();
//        
//        Map<String, Long> distribution = new HashMap<>();
//        distribution.put("5_stars", totalFiveStars);
//        distribution.put("4_stars", totalFourStars);
//        distribution.put("3_stars", totalThreeStars);
//        distribution.put("2_stars", totalTwoStars);
//        distribution.put("1_star", totalOneStars);
//        
//        return new RatingStatisticsDTO(
//                totalRatings,
//                overallAverage,
//                totalFiveStars,
//                totalFourStars,
//                totalThreeStars,
//                totalTwoStars,
//                totalOneStars,
//                distribution
//        );
//    }
    
    @Override
    @Transactional(readOnly = true)
    public List<RatingResponseDTO> getRatingsByScore(Integer score) {
        log.info("Fetching ratings with score: {}", score);
        return ratingRepository.findByScore(score).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<RatingResponseDTO> getRecentRatings(int days) {
        log.info("Fetching ratings from last {} days", days);
        LocalDate cutoffDate = LocalDate.now().minusDays(days);
        return ratingRepository.findRecentRatings(cutoffDate).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<RatingResponseDTO> getAllRatings() {
        log.info("Fetching all ratings");
        return ratingRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Double calculateAverageRating(Long villaId) {
        Double average = ratingRepository.calculateAverageRating(villaId);
        return average != null ? average : 0.0;
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getTotalRatingCount() {
        return ratingRepository.count();
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getRatingCountByVilla(Long villaId) {
        return ratingRepository.countByVillaId(villaId);
    }
    
    // ========== HELPER METHODS ==========
    
    private RatingResponseDTO mapToResponseDTO(Rating rating) {
        return new RatingResponseDTO(
                rating.getId(),
                rating.getUser().getId(),
                rating.getUser().getName(),
                rating.getVilla().getId(),
                rating.getVilla().getName(),
                rating.getScore(),
                rating.getFeedback(),
                rating.getRatingDate() != null ? rating.getRatingDate().toString() : null,
                rating.getCreatedAt() != null ? rating.getCreatedAt().toString() : null,
                rating.getUpdatedAt() != null ? rating.getUpdatedAt().toString() : null
        );
    }
    
    private RatingDetailDTO mapToDetailDTO(Rating rating) {
        RatingDetailDTO dto = new RatingDetailDTO();
        dto.setId(rating.getId());
        dto.setUserId(rating.getUser().getId());
        dto.setUserName(rating.getUser().getName());
        dto.setUserEmail(rating.getUser().getEmail());
        dto.setVillaId(rating.getVilla().getId());
        dto.setVillaName(rating.getVilla().getName());
        dto.setVillaAddress(rating.getVilla().getAddress());
        dto.setScore(rating.getScore());
        dto.setFeedback(rating.getFeedback());
        dto.setRatingDate(rating.getRatingDate());
        dto.setCreatedAt(rating.getCreatedAt());
        dto.setUpdatedAt(rating.getUpdatedAt());
        return dto;
    }
    
    private SimpleRatingDTO mapToSimpleDTO(Rating rating) {
        return new SimpleRatingDTO(
                rating.getId(),
                rating.getUser().getName(),
                rating.getScore(),
                rating.getFeedback(),
                rating.getRatingDate()
        );
    }
}