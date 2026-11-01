package com.sentinel.api.dto;

import jakarta.validation.constraints.NotNull;

public record UpdateAlertRuleEnabledRequest(
    @NotNull Boolean enabled
) {
}