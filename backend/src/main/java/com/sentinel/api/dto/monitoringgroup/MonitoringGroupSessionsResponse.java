package com.sentinel.api.dto.monitoringgroup;

import java.util.List;

public record MonitoringGroupSessionsResponse(
        List<MonitoringParticipantResponse> participants,
        int total
) {
}
