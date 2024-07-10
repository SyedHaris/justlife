package com.example.justlife.repository;

import com.example.justlife.model.CleaningProfessional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CleaningProfessionalRepository extends JpaRepository<CleaningProfessional, Long> {

    List<CleaningProfessional> findByIdIn(List<Long> cleaningProfessionalIds);

    List<CleaningProfessional> findByIdNotIn(List<Long> cleaningProfessionalIds, Pageable pageable);

}