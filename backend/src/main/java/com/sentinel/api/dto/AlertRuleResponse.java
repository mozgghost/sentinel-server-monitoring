package com.sentinel.api.dto;

import com.sentinel.domain.AlertRule;
import com.sentinel.domain.AlertSeverity;

import java.time.Instant;
import java.util.UUID;

public record AlertRuleResponse(
    UUID id,
    String name,
    String description,
    String metric,
    String operator,
    double threshold,
    AlertSeverity severity,
    boolean enabled,
    Instant lastTriggered,
    Instant createdAt
) {
    public static AlertRuleResponse from(AlertRule rule) {
        return new AlertRuleResponse(
            rule.getId(),
            rule.getName(),
            rule.getDescription(),
            rule.getMetric(),
            rule.getOperator(),
            rule.getThreshold(),
            rule.getSeverity(),
            rule.isEnabled(),
            rule.getLastTriggered(),
            rule.getCreatedAt()
        );
    }
}