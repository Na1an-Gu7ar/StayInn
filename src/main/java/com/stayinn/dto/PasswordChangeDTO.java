package com.stayinn.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class PasswordChangeDTO {

	@NotBlank(message = "Email is required")
	private String email;
	
	@NotBlank(message = "new password is required")
	@Size(min = 6, max =50, message = "Password is between 6 and 50 characters")
	private String newPassword;
}
