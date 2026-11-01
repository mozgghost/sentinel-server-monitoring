package com.sentinel.api.dto.monitoringgroup;

import java.util.List;
import java.util.UUID;

public record MonitoringParticipantResponse(
        UUID id,
        String type,
        String name,
        List<MonitoringItemResponse> items,
        int total,
        boolean isSubmitted
) {
}
