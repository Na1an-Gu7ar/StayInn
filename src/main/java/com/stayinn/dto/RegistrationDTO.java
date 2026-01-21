package com.stayinn.dto;

import com.stayinn.entities.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor

public class RegistrationDTO {

	@NotBlank(message = "Email is required")
	@Email(message = "Invalid email format")
	private String email;

	@NotBlank(message = "password is required")
	@Size(min = 5, max =15, message = "password should be between 6 to 50" )
	private String password;
	
	
	@NotBlank(message = "name is required")
	@Size(min = 2, max = 10,message = "name should be between 2 to 10")
	private String name;
	
	@Pattern(regexp = "^[0-9]{10,15}$", message = "Phone number must be 10-15 digits")
	private String mobile;
	
//	private Boolean active;
	
	private Role role;
}
