package com.stayinn.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.stayinn.entities.Role;
import com.stayinn.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    List<User> findByRole(Role role);
    
    List<User> findByActive(boolean active);
    
    List<User> findByNameContainingIgnoreCase(String name);
    
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.bookings WHERE u.id = :userId")
    Optional<User> findByIdWithBookings(@Param("userId") Long userId);
    
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.ratings WHERE u.id = :userId")
    Optional<User> findByIdWithRatings(@Param("userId") Long userId);
    
    long countByRole(Role role);
}