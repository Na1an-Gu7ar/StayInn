package com.stayinn.dto.User;

import java.time.LocalDateTime;

import com.stayinn.entities.Role;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class UserResponseDTO {

	private Long id;
	private String email;
	private String name;
	private String mobile;
	private Role role;
	private boolean active;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
