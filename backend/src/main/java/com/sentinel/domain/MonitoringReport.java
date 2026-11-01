package com.sentinel.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
    name = "monitoring_reports",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_monitoring_report_group",
            columnNames = "group_id"
        )
    }
)
public class MonitoringReport {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private MonitoringGroup group;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MonitoringReportStatus status = MonitoringReportStatus.DRAFT;

    @Column(nullable = false)
    private Instant createdAt;

    private Instant submittedAt;

    protected MonitoringReport() {
    }

    public MonitoringReport(MonitoringGroup group) {
        this.group = group;
        this.status = MonitoringReportStatus.DRAFT;
        this.createdAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public MonitoringGroup getGroup() {
        return group;
    }

    public MonitoringReportStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getSubmittedAt() {
        return submittedAt;
    }

    public void submit() {
        if (status != MonitoringReportStatus.DRAFT) {
            throw new IllegalStateException(
                    "Only draft reports can be submitted"
            );
        }

        this.status = MonitoringReportStatus.SUBMITTED;
        this.submittedAt = Instant.now();
    }
}