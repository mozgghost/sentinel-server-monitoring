package com.sentinel.api.dto.monitoringgroup;

import com.sentinel.domain.AlertSeverity;
import java.util.UUID;

public record MonitoringItemResponse(
        UUID id,
        String name,
        AlertSeverity severity,
        int quantity
) {
}
