package com.sentinel.api.dto.monitoringgroup;

import java.util.UUID;

public record MonitoringGroupMemberCreatedResponse(
        UUID sessionId,
        UUID serverId,
        String serverName,
        boolean isSubmitted
) {
}
