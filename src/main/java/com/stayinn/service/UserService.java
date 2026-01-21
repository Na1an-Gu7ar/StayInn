package com.stayinn.service;

import java.util.List;

import com.stayinn.dto.PasswordChangeDTO;
import com.stayinn.dto.UserProfileDTO;
import com.stayinn.dto.UserResponseDTO;
import com.stayinn.dto.UserUpdateDTO;
import com.stayinn.entities.Role;
import com.stayinn.dto.*;

public interface UserService {
    
    /**
     * Register a new user
     * @param registrationDTO user registration data
     * @return created user details
     * @throws RuntimeException if email already exists
     */
    UserResponseDTO registerUser(RegistrationDTO registrationDTO);
    
    /**
     * Get user by ID
     * @param id user ID
     * @return user details
     * @throws RuntimeException if user not found
     */
    UserResponseDTO getUserById(Long id);
    
    /**
     * Get user by email
     * @param email user email
     * @return user details
     * @throws RuntimeException if user not found
     */
    UserResponseDTO getUserByEmail(String email);
    
    /**
     * Get user profile with booking and rating counts
     * @param id user ID
     * @return detailed user profile
     * @throws RuntimeException if user not found
     */
    UserProfileDTO getUserProfile(Long id);
    
    /**
     * Update user details
     * @param id user ID
     * @param updateDTO update data (only non-null fields updated)
     * @return updated user details
     * @throws RuntimeException if user not found or email already exists
     */
    UserResponseDTO updateUser(Long id, UserUpdateDTO updateDTO);
    
    /**
     * Change user password
     * @param id user ID
     * @param passwordChangeDTO password change data
     * @throws RuntimeException if current password incorrect or passwords don't match
     */
    void changePassword(Long id, PasswordChangeDTO passwordChangeDTO);
    
    /**
     * Deactivate user account (soft delete)
     * @param id user ID
     * @throws RuntimeException if user not found
     */
    void deactivateUser(Long id);
    
    /**
     * Activate user account
     * @param id user ID
     * @throws RuntimeException if user not found
     */
    void activateUser(Long id);
    
    /**
     * Delete user permanently (hard delete)
     * @param id user ID
     * @throws RuntimeException if user not found
     */
    void deleteUser(Long id);
    
    /**
     * Get all users (Admin only)
     * @return list of all users
     */
    List<UserResponseDTO> getAllUsers();
    
    /**
     * Get all users by role
     * @param role USER or ADMIN
     * @return list of users with specified role
     */
    List<UserResponseDTO> getUsersByRole(Role role);
    
    /**
     * Get all active users
     * @return list of active users
     */
    List<UserResponseDTO> getActiveUsers();
    
    /**
     * Search users by name
     * @param name partial or full name
     * @return list of matching users
     */
    List<UserResponseDTO> searchUsersByName(String name);
    
    /**
     * Check if email exists
     * @param email email to check
     * @return true if exists, false otherwise
     */
    boolean existsByEmail(String email);
    
    /**
     * Get total user count
     * @return total number of users
     */
    long getTotalUserCount();
    
    /**
     * Get user count by role
     * @param role USER or ADMIN
     * @return count of users with specified role
     */
    long getUserCountByRole(Role role);
}