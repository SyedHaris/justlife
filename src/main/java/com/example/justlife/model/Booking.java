package com.example.justlife.model;

import com.example.justlife.enums.BookingStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class Booking extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "start_time", nullable = false)
    private LocalTime start_time;

    @Column(name = "end_time", nullable = false)
    private LocalTime end_time;

    @Enumerated
    @Column(name = "status", nullable = false)
    private BookingStatus status;

    @OneToOne(orphanRemoval = true)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToMany
    @JoinTable(name = "booking_cleaning_professional",
            joinColumns = @JoinColumn(name = "booking_id"),
            inverseJoinColumns = @JoinColumn(name = "cp_id"))
    private Set<CleaningProfessional> cleaning_professionals = new LinkedHashSet<>();
}
