package com.stayinn.repository;


import com.stayinn.entities.Villa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VillaRepository extends JpaRepository<Villa, Long> {
    
    
    List<Villa> findByNameContainingIgnoreCase(String name);
    
   
    List<Villa> findByAddressContainingIgnoreCase(String address);
    
    
    List<Villa> findByPricePerNightBetween(Double minPrice, Double maxPrice);
    
    
    List<Villa> findByPricePerNightLessThanEqual(Double maxPrice);
    
    
    List<Villa> findByPricePerNightGreaterThanEqual(Double minPrice);
    
    
    @Query("SELECT v FROM Villa v LEFT JOIN FETCH v.ratings WHERE v.id = :villaId")
    Optional<Villa> findByIdWithRatings(@Param("villaId") Long villaId);
    
   
    @Query("SELECT v FROM Villa v WHERE " +
           "LOWER(v.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(v.address) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Villa> searchVillas(@Param("keyword") String keyword);
    
    
    List<Villa> findAllByOrderByPricePerNightAsc();
    
    
    List<Villa> findAllByOrderByPricePerNightDesc();
    
    
    @Query("SELECT v FROM Villa v WHERE " +
           "(:name IS NULL OR LOWER(v.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:address IS NULL OR LOWER(v.address) LIKE LOWER(CONCAT('%', :address, '%'))) AND " +
           "(:minPrice IS NULL OR v.pricePerNight >= :minPrice) AND " +
           "(:maxPrice IS NULL OR v.pricePerNight <= :maxPrice)")
    List<Villa> findVillasWithFilters(
            @Param("name") String name,
            @Param("address") String address,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice
    );
    
    
    long countByPricePerNightBetween(Double minPrice, Double maxPrice);
    
    
    boolean existsByName(String name);
    
   
    @Query("SELECT v FROM Villa v LEFT JOIN v.ratings r " +
           "GROUP BY v.id " +
           "ORDER BY AVG(r.score) DESC")
    List<Villa> findTopRatedVillas();
}
