package com.sentinel.repository;

import com.sentinel.domain.MonitoringReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MonitoringReportRepository
        extends JpaRepository<MonitoringReport, UUID> {

    Optional<MonitoringReport> findByGroup_Id(UUID groupId);
}