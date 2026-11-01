package com.sentinel.repository;

import com.sentinel.domain.MonitoringReportItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MonitoringReportItemRepository
        extends JpaRepository<MonitoringReportItem, UUID> {

    List<MonitoringReportItem> findAllByReport_Id(UUID reportId);
}