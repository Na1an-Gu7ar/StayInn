package com.stayinn.service;

import java.util.List;

import com.stayinn.entities.Role;
import com.stayinn.dto.*;
import com.stayinn.dto.User.UserProfileDTO;
import com.stayinn.dto.User.UserResponseDTO;
import com.stayinn.dto.User.UserUpdateDTO;

public interface UserService {
    
    
    UserResponseDTO registerUser(RegistrationDTO registrationDTO);
    
    
    UserResponseDTO getUserById(Long id);
    
    
    UserResponseDTO getUserByEmail(String email);
    
    UserProfileDTO getUserProfile(Long id);
    
    
    UserResponseDTO updateUser(Long id, UserUpdateDTO updateDTO);
    
    
    void changePassword(PasswordChangeDTO passwordChangeDTO);
    
    
    void deactivateUser(Long id);
    
    
    void activateUser(Long id);
    
    
    void deleteUser(Long id);
    

    List<UserResponseDTO> getAllUsers();
    
    
    List<UserResponseDTO> getUsersByRole(Role role);
    
   
    List<UserResponseDTO> getActiveUsers();
    
   
    List<UserResponseDTO> searchUsersByName(String name);
    
    
    boolean existsByEmail(String email);
    
    
    long getTotalUserCount();
    
    
    long getUserCountByRole(Role role);
}