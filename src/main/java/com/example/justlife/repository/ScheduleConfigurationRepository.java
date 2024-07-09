package com.example.justlife.repository;

import com.example.justlife.model.ScheduleConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleConfigurationRepository extends JpaRepository<ScheduleConfiguration, Long> {
}