package com.stayinn.entities;

import java.time.LocalDate;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@RequiredArgsConstructor
@AttributeOverride(name = "id",column = @Column(name = "rating_id"))
@Getter
@Setter
@Table(name = "ratings") // Explicit table name
public class Rating extends BaseEntity{


    private Integer score; // e.g., 1 to 5
    private String feedback; // Text review
    private LocalDate ratingDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // Who gave the rating

    @ManyToOne
    @JoinColumn(name = "villa_id")
    private Villa villa; // Which villa was rated
}
