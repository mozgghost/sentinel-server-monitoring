package com.sentinel.api.dto.monitoringgroup;

import com.sentinel.domain.AlertSeverity;

public record ConsolidatedMonitoringItemResponse(
        String incidentType,
        AlertSeverity severity,
        int quantity
) {
}