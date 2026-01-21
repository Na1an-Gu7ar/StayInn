package com.stayinn.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stayinn.dto.PasswordChangeDTO;
import com.stayinn.dto.RegistrationDTO;
import com.stayinn.dto.UserProfileDTO;
import com.stayinn.dto.UserResponseDTO;
import com.stayinn.dto.UserUpdateDTO;
import com.stayinn.entities.Role;
import com.stayinn.entities.User;
import com.stayinn.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public UserResponseDTO registerUser(RegistrationDTO registrationDTO) {
        log.info("Registering new user with email: {}", registrationDTO.getEmail());
        
        // Check if email already exists
        if (userRepository.existsByEmail(registrationDTO.getEmail())) {
            log.error("Email already exists: {}", registrationDTO.getEmail());
            throw new RuntimeException("Email already registered");
        }
        
        // Create new user
        User user = new User();
        user.setEmail(registrationDTO.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));
        user.setName(registrationDTO.getName());
        user.setMobile(registrationDTO.getMobile());
        user.setActive(true);
        user.setRole(registrationDTO.getRole() != null ? registrationDTO.getRole() : Role.USER);
        
        User savedUser = userRepository.save(user);
//        log.info("User registered successfully with ID: {}", savedUser.getId());
        
        return mapToResponseDTO(savedUser);
    }
    
    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(Long id) {
        log.info("Fetching user with ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
        return mapToResponseDTO(user);
    }
    
    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUserByEmail(String email) {
        log.info("Fetching user with email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        return mapToResponseDTO(user);
    }
    
    @Override
    @Transactional(readOnly = true)
    public UserProfileDTO getUserProfile(Long id) {
        log.info("Fetching profile for user ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
        
        UserProfileDTO profileDTO = new UserProfileDTO();
//        profileDTO.setId(user.getId());
        profileDTO.setEmail(user.getEmail());
        profileDTO.setName(user.getName());
        profileDTO.setPhoneNumber(user.getMobile());
        profileDTO.setRole(user.getRole());
        profileDTO.setActive(user.getActive());
//        profileDTO.setCreatedAt(user.getCreatedAt());
        profileDTO.setTotalBookings(user.getBookings() != null ? user.getBookings().size() : 0);
        profileDTO.setTotalRatings(user.getRatings() != null ? user.getRatings().size() : 0);
        
        return profileDTO;
    }
    
    @Override
    public UserResponseDTO updateUser(Long id, UserUpdateDTO updateDTO) {
        log.info("Updating user with ID: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
        
        // Update email if provided and different
        if (updateDTO.getEmail() != null && !updateDTO.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(updateDTO.getEmail())) {
                throw new RuntimeException("Email already in use");
            }
            user.setEmail(updateDTO.getEmail());
        }
        
        // Update name if provided
        if (updateDTO.getName() != null) {
            user.setName(updateDTO.getName());
        }
        
        // Update phone number if provided
        if (updateDTO.getMobile() != null) {
            user.setMobile(updateDTO.getMobile());
        }
        
        // Update password if provided
        if (updateDTO.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(updateDTO.getPassword()));
        }
        
        User updatedUser = userRepository.save(user);
//        log.info("User updated successfully with ID: {}", updatedUser.getId());
        
        return mapToResponseDTO(updatedUser);
    }
    
    @Override
    public void changePassword(Long id, PasswordChangeDTO passwordChangeDTO) {
        log.info("Changing password for user ID: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
        
        // Verify current password
        if (!passwordEncoder.matches(passwordChangeDTO.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }
        
        // Verify new password confirmation
        if (!passwordChangeDTO.getNewPassword().equals(passwordChangeDTO.getConfirmPassword())) {
            throw new RuntimeException("New passwords do not match");
        }
        
        // Update password
        user.setPassword(passwordEncoder.encode(passwordChangeDTO.getNewPassword()));
        userRepository.save(user);
        
        log.info("Password changed successfully for user ID: {}", id);
    }
    
    @Override
    public void deactivateUser(Long id) {
        log.info("Deactivating user with ID: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
        
        user.setActive(false);
        userRepository.save(user);
        
        log.info("User deactivated successfully with ID: {}", id);
    }
    
    @Override
    public void activateUser(Long id) {
        log.info("Activating user with ID: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
        
        user.setActive(true);
        userRepository.save(user);
        
        log.info("User activated successfully with ID: {}", id);
    }
    
    @Override
    public void deleteUser(Long id) {
        log.info("Deleting user with ID: {}", id);
        
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with ID: " + id);
        }
        
        userRepository.deleteById(id);
        log.info("User deleted successfully with ID: {}", id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllUsers() {
        log.info("Fetching all users");
        return userRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getUsersByRole(Role role) {
        log.info("Fetching users with role: {}", role);
        return userRepository.findByRole(role).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getActiveUsers() {
        log.info("Fetching active users");
        return userRepository.findByActive(true).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> searchUsersByName(String name) {
        log.info("Searching users with name: {}", name);
        return userRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getTotalUserCount() {
        return userRepository.count();
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getUserCountByRole(Role role) {
        return userRepository.countByRole(role);
    }
    
    // Helper method to map Entity to DTO
    private UserResponseDTO mapToResponseDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getMobile(),
                user.getRole(),
                user.getActive(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}