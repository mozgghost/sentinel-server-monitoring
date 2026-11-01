package com.sentinel.api.dto.monitoringgroup;

import com.sentinel.domain.MonitoringReportStatus;

import java.util.List;
import java.util.UUID;

public record MonitoringReportSubmittedResponse(
        UUID reportId,
        MonitoringReportStatus status,
        List<ConsolidatedMonitoringItemResponse> items,
        int total
) {
}