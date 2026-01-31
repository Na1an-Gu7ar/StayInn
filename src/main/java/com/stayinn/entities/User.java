package com.stayinn.entities;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@RequiredArgsConstructor
@AttributeOverride(name = "id",column = @Column(name = "user_id"))
@Getter
@Setter
@Table(name = "users") // Explicit table name
public class User extends BaseEntity{

	    
	    private String email;
	    private String password; // BCrypt encoded
	    private String name;
	    private String mobile;
	    private Boolean active;
	    
	    @Enumerated(EnumType.STRING)
	    private Role role; // ADMIN, USER

	    @OneToMany(mappedBy = "user")
	    @JsonIgnore
	    private List<Booking> bookings;

	    @OneToMany(mappedBy = "user")
	    @JsonIgnore
	    private List<Rating> ratings; // History of ratings given by this user
	    
	    
	}
	
