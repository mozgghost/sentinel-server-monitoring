package com.sentinel.api.dto.monitoringgroup;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record SubmitMonitoringGroupRequest(
        @NotNull UUID reportId
) {
}