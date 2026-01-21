package com.stayinn.dto;

import java.time.LocalDateTime;

import com.stayinn.entities.Role;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class UserProfileDTO {

	private Long id;
	private String email;
	private String name;
    private String phoneNumber;
	private Role role;
	private Boolean active;
	private LocalDateTime createdAt;
	private int totalBookings;
	private int totalRatings;
}
