package com.example.justlife.repository;

import com.example.justlife.model.BookedSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface BookedSlotRepository extends JpaRepository<BookedSlot, Long> {

    @Query("""
            FROM BookedSlot b WHERE  (:id IS NULL OR b.booking.id != :id)
            AND b.cleaningProfessional.id IN (:cleanerProfessionalIds)
            AND b.date = :date
            AND ((b.startTime >= :startTime
            AND b.endTime <= :endTime) OR b.startTime = :endTime)
            """)
    List<BookedSlot> findBookedCleanerSlotsInDateTimeRange(LocalDate date, LocalTime startTime, LocalTime endTime, List<Long> cleanerProfessionalIds, Long id);

    List<BookedSlot> findByBookingId(Long id);

}