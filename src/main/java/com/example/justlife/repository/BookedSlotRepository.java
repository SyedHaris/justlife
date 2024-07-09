package com.example.justlife.repository;

import com.example.justlife.model.BookedSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface BookedSlotRepository extends JpaRepository<BookedSlot, Long> {

    @Query("""
            FROM BookedSlot b JOIN b.cleaningProfessional cp
            WHERE cp.id IN (:cleanerProfessionalIds)
            AND b.date = :date
            AND ((startTime > :startTime
            AND endTime <= :endTime) OR startTime = :endTime)
            """)
    List<BookedSlot> findBookedCleanerSlotsInDateTimeRange(LocalDate date, LocalTime startTime, LocalTime endTime, List<Long> cleanerProfessionalIds);

}