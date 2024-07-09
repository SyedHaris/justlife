package com.example.justlife.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;


@Entity
@Getter
@Setter
public class CleaningProfessional extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "image_url")
    private String image_url;

    @Column(name = "rating")
    private Double rating;

    @OneToOne(orphanRemoval = true)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @ManyToMany(mappedBy = "cleaning_professionals")
    private Set<Booking> bookings = new LinkedHashSet<>();
}
