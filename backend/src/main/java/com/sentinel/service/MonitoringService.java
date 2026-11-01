package com.sentinel.service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.sentinel.api.dto.CreateServerRequest;
import com.sentinel.api.dto.IncidentResponse;
import com.sentinel.api.dto.MetricIngestRequest;
import com.sentinel.api.dto.ServerResponse;
import com.sentinel.api.dto.UpdateServerRequest;
import com.sentinel.domain.AlertRule;
import com.sentinel.domain.AlertSeverity;
import com.sentinel.domain.AlertStatus;
import com.sentinel.domain.Incident;
import com.sentinel.domain.Metric;
import com.sentinel.domain.ServerNode;
import com.sentinel.domain.ServerStatus;
import com.sentinel.repository.AlertRuleRepository;
import com.sentinel.repository.IncidentRepository;
import com.sentinel.repository.MetricRepository;
import com.sentinel.repository.ServerRepository;

@Service
public class MonitoringService {

    private static final List<AlertStatus> ACTIVE_STATUSES = List.of(
            AlertStatus.OPEN,
            AlertStatus.ACKNOWLEDGED
    );

    private final ServerRepository servers;
    private final MetricRepository metrics;
    private final IncidentRepository incidents;
    private final AlertRuleRepository alertRules;

    public MonitoringService(
            ServerRepository servers,
            MetricRepository metrics,
            IncidentRepository incidents,
            AlertRuleRepository alertRules) {
        this.servers = servers;
        this.metrics = metrics;
        this.incidents = incidents;
        this.alertRules = alertRules;
    }

    @Transactional(readOnly = true)
    public List<ServerResponse> listServers() {
        return servers.findAll()
                .stream()
                .map(ServerResponse::from)
                .toList();
    }

    @Transactional
    public ServerResponse createServer(CreateServerRequest request) {
        return ServerResponse.from(
                servers.save(
                        new ServerNode(
                                request.name(),
                                request.host(),
                                request.environment()
                        )
                )
        );
    }

    @Transactional
    public ServerResponse updateServer(UUID id, UpdateServerRequest request) {
        ServerNode server = servers.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Server not found"
                ));

        server.updateDetails(
                request.name().trim(),
                request.host().trim(),
                request.environment().trim()
        );

        return ServerResponse.from(servers.save(server));
    }

    @Transactional
    public void deleteServer(UUID id) {
        if (!servers.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Server not found"
            );
        }

        servers.deleteById(id);
    }

    @Transactional
    public void ingest(MetricIngestRequest request) {
        ServerNode server = servers.findById(request.serverId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Server not found"
                ));

        Instant recordedAt = request.recordedAt();
        double cpu = request.cpuPercent();
        double memory = request.memoryPercent();

        // Persist the latest metrics before evaluating alert rules.
        server.updateMetrics(
                cpu,
                memory,
                recordedAt,
                ServerStatus.HEALTHY
        );

        metrics.save(new Metric(server, cpu, memory, recordedAt));

        // Evaluate every rule, including disabled rules.
        // Disabled rules must resolve their previously active incidents.
        List<AlertRule> rules = alertRules.findAll();

        for (AlertRule rule : rules) {
            double value;

            if ("CPU".equalsIgnoreCase(rule.getMetric())) {
                value = cpu;
            } else if ("MEMORY".equalsIgnoreCase(rule.getMetric())) {
                value = memory;
            } else {
                // Unsupported metric types are not evaluated.
                continue;
            }

            boolean conditionMet = rule.isEnabled()
                    && evaluateCondition(
                            value,
                            rule.getOperator(),
                            rule.getThreshold()
                    );

            if (conditionMet) {
                boolean created = createIncidentIfAbsent(
                        server,
                        rule.getSeverity(),
                        rule.getName(),
                        buildRuleDescription(rule, value)
                );

                if (created) {
                    rule.markTriggered(recordedAt);
                    alertRules.save(rule);
                }
            } else {
                resolveActiveIncidents(
                        server.getId(),
                        rule.getName()
                );
            }
        }

        // Determine server status from all currently active incidents.
        List<Incident> activeIncidents =
                incidents.findAllByServer_IdAndStatusIn(
                        server.getId(),
                        ACTIVE_STATUSES
                );

        ServerStatus status = determineServerStatus(activeIncidents);

        server.updateMetrics(cpu, memory, recordedAt, status);
        servers.save(server);
    }

    private boolean evaluateCondition(
            double value,
            String operator,
            double threshold) {

        return switch (operator) {
            case ">" -> value > threshold;
            case ">=" -> value >= threshold;
            default -> false;
        };
    }

    private String buildRuleDescription(AlertRule rule, double actualValue) {
        String metricName = rule.getMetric().equalsIgnoreCase("CPU")
                ? "CPU usage"
                : "Memory usage";

        return metricName
                + " reached "
                + actualValue
                + "% (rule: "
                + rule.getOperator()
                + " "
                + rule.getThreshold()
                + "%).";
    }

    private boolean createIncidentIfAbsent(
            ServerNode server,
            AlertSeverity severity,
            String title,
            String description) {

        boolean alreadyExists =
                incidents.existsByServer_IdAndTitleAndStatusIn(
                        server.getId(),
                        title,
                        ACTIVE_STATUSES
                );

        if (alreadyExists) {
            return false;
        }

        incidents.save(
                new Incident(
                        server,
                        severity,
                        title,
                        description
                )
        );

        return true;
    }

    private void resolveActiveIncidents(UUID serverId, String title) {
        List<Incident> activeIncidents =
                incidents.findAllByServer_IdAndTitleAndStatusIn(
                        serverId,
                        title,
                        ACTIVE_STATUSES
                );

        activeIncidents.forEach(Incident::resolve);

        if (!activeIncidents.isEmpty()) {
            incidents.saveAll(activeIncidents);
        }
    }

    private ServerStatus determineServerStatus(List<Incident> activeIncidents) {
        boolean hasCritical = activeIncidents.stream()
                .anyMatch(incident ->
                        incident.getSeverity() == AlertSeverity.CRITICAL
                );

        if (hasCritical) {
            return ServerStatus.CRITICAL;
        }

        boolean hasWarning = activeIncidents.stream()
                .anyMatch(incident ->
                        incident.getSeverity() == AlertSeverity.WARNING
                );

        if (hasWarning) {
            return ServerStatus.WARNING;
        }

        return ServerStatus.HEALTHY;
    }

    @Transactional(readOnly = true)
    public List<IncidentResponse> listIncidents() {
        return incidents.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(IncidentResponse::from)
                .toList();
    }

    @Transactional
    public IncidentResponse acknowledge(UUID id) {
        Incident incident = incidents.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Incident not found"
                ));

        incident.acknowledge();

        return IncidentResponse.from(incidents.save(incident));
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> history(UUID serverId) {
        if (!servers.existsById(serverId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Server not found"
            );
        }

        return metrics.findTop100ByServer_IdOrderByRecordedAtDesc(serverId)
                .stream()
                .sorted(Comparator.comparing(Metric::getRecordedAt))
                .map(metric -> Map.<String, Object>of(
                        "recordedAt", metric.getRecordedAt(),
                        "cpuPercent", metric.getCpuPercent(),
                        "memoryPercent", metric.getMemoryPercent()
                ))
                .toList();
    }
}