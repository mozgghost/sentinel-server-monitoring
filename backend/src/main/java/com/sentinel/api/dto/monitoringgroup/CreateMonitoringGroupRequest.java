package com.sentinel.api.dto.monitoringgroup;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateMonitoringGroupRequest(
        @NotBlank
        @Size(max = 120)
        String name
) {
}
