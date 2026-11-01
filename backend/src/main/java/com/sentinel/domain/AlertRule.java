package com.sentinel.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
    name = "alert_rules",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_alert_rule_name",
        columnNames = "name"
    )
)
public class AlertRule {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(nullable = false, length = 20)
    private String metric;

    @Column(nullable = false, length = 5)
    private String operator;

    @Column(nullable = false)
    private double threshold;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AlertSeverity severity;

    @Column(nullable = false)
    private boolean enabled = true;

    private Instant lastTriggered;

    @Column(nullable = false)
    private Instant createdAt;

    protected AlertRule() {
    }

    public AlertRule(
            String name,
            String description,
            String metric,
            String operator,
            double threshold,
            AlertSeverity severity) {

        this.name = name;
        this.description = description;
        this.metric = metric;
        this.operator = operator;
        this.threshold = threshold;
        this.severity = severity;
        this.enabled = true;
        this.createdAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getMetric() {
        return metric;
    }

    public String getOperator() {
        return operator;
    }

    public double getThreshold() {
        return threshold;
    }

    public AlertSeverity getSeverity() {
        return severity;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Instant getLastTriggered() {
        return lastTriggered;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void markTriggered(Instant at) {
        this.lastTriggered = at;
    }
}