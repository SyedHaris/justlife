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
            AND (:cleanerProfessionalIds IS NULL OR b.cleaningProfessional.id IN (:cleanerProfessionalIds))
            AND b.date = :date
            AND ((b.startTime >= :startTimeWithOverlap
            AND b.endTime <= :endTimeWithOverlap) OR b.endTime = :startTime)
            """)
    List<BookedSlot> findBookedCleanerSlotsInDateTimeRange(LocalDate date, LocalTime startTimeWithOverlap, LocalTime endTimeWithOverlap, LocalTime startTime, List<Long> cleanerProfessionalIds, Long id);

    List<BookedSlot> findByBookingId(Long id);

    @Query("""
            FROM BookedSlot b WHERE b.cleaningProfessional.id = :cleaningProfessionalId
            AND b.date = :date
            ORDER BY b.startTime ASC
            """)
    List<BookedSlot> findByCleaningProfessionalIdAndDate(Long cleaningProfessionalId, LocalDate date);

}