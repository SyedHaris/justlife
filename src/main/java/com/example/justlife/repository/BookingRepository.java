package com.example.justlife.repository;

import com.example.justlife.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    Optional<Booking> findFirstByIdAndCustomerId(Long id, Long customerId);

}