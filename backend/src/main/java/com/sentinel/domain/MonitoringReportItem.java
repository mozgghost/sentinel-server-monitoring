package com.sentinel.domain;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "monitoring_report_items")
public class MonitoringReportItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false)
    private MonitoringReport report;

    @Column(nullable = false)
    private String incidentType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlertSeverity severity;

    @Column(nullable = false)
    private int quantity;

    protected MonitoringReportItem() {
    }

    public MonitoringReportItem(
            MonitoringReport report,
            String incidentType,
            AlertSeverity severity,
            int quantity
    ) {
        this.report = report;
        this.incidentType = incidentType;
        this.severity = severity;
        this.quantity = quantity;
    }

    public UUID getId() {
        return id;
    }

    public MonitoringReport getReport() {
        return report;
    }

    public String getIncidentType() {
        return incidentType;
    }

    public AlertSeverity getSeverity() {
        return severity;
    }

    public int getQuantity() {
        return quantity;
    }
}