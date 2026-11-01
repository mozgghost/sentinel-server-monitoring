package com.sentinel.api.dto.monitoringgroup;

import java.util.UUID;

public record MonitoringGroupCreatedResponse(
        UUID groupId,
        String name,
        UUID ownerSessionId,
        UUID reportId
) {
}