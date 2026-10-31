
package com.sentinel.service;

import com.sentinel.api.dto.*;
import com.sentinel.domain.*;
import com.sentinel.repository.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.*;

@Service
public class MonitoringService {

    private final ServerRepository servers;
    private final MetricRepository metrics;
    private final IncidentRepository incidents;

    @Value("${sentinel.alert.cpu-warning:75}")
    double cpuWarning;

    @Value("${sentinel.alert.cpu-critical:90}")
    double cpuCritical;

    @Value("${sentinel.alert.memory-warning:80}")
    double memWarning;

    @Value("${sentinel.alert.memory-critical:92}")
    double memCritical;

    public MonitoringService(
            ServerRepository s,
            MetricRepository m,
            IncidentRepository i) {
        servers = s;
        metrics = m;
        incidents = i;
    }

    public List<ServerResponse> listServers() {
        return servers.findAll()
                .stream()
                .map(ServerResponse::from)
                .toList();
    }

    public ServerResponse createServer(CreateServerRequest r) {
        return ServerResponse.from(
                servers.save(
                        new ServerNode(
                                r.name(),
                                r.host(),
                                r.environment())));
    }

    public void deleteServer(UUID id) {
        if (!servers.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Server not found");
        }

        servers.deleteById(id);
    }

    @Transactional
    public void ingest(MetricIngestRequest r) {

        ServerNode s = servers.findById(r.serverId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Server not found"));

        Instant at = r.recordedAt();

        double cpu = r.cpuPercent();
        double memory = r.memoryPercent();

        // Determine CPU severity independently.
        AlertSeverity cpuSeverity = null;

        if (cpu >= cpuCritical) {
            cpuSeverity = AlertSeverity.CRITICAL;
        } else if (cpu >= cpuWarning) {
            cpuSeverity = AlertSeverity.WARNING;
        }

        // Determine memory severity independently.
        AlertSeverity memorySeverity = null;

        if (memory >= memCritical) {
            memorySeverity = AlertSeverity.CRITICAL;
        } else if (memory >= memWarning) {
            memorySeverity = AlertSeverity.WARNING;
        }

        // Determine overall server status.
        ServerStatus status;

        if (cpuSeverity == AlertSeverity.CRITICAL ||
                memorySeverity == AlertSeverity.CRITICAL) {
            status = ServerStatus.CRITICAL;
        } else if (cpuSeverity == AlertSeverity.WARNING ||
                memorySeverity == AlertSeverity.WARNING) {
            status = ServerStatus.WARNING;
        } else {
            status = ServerStatus.HEALTHY;
        }

        // Save current server metrics.
        s.updateMetrics(cpu, memory, at, status);
        servers.save(s);

        metrics.save(
                new Metric(s, cpu, memory, at));

        // Resolve CPU incidents independently.
        if (cpu < cpuWarning) {
            resolveActiveIncidents(
                    s.getId(),
                    List.of(
                            "High CPU usage",
                            "Elevated CPU usage"));
        }

        // Resolve memory incidents independently.
        if (memory < memWarning) {
            resolveActiveIncidents(
                    s.getId(),
                    List.of(
                            "High memory usage",
                            "Elevated memory usage"));
        }

        // Create CPU incident if necessary.
        if (cpuSeverity != null) {

            String title = cpuSeverity == AlertSeverity.CRITICAL
                    ? "High CPU usage"
                    : "Elevated CPU usage";

            String description = "CPU usage reached " + cpu + "%.";

            createIncidentIfAbsent(
                    s,
                    cpuSeverity,
                    title,
                    description);
        }

        // Create memory incident if necessary.
        if (memorySeverity != null) {

            String title = memorySeverity == AlertSeverity.CRITICAL
                    ? "High memory usage"
                    : "Elevated memory usage";

            String description = "Memory usage reached " + memory + "%.";

            createIncidentIfAbsent(
                    s,
                    memorySeverity,
                    title,
                    description);
        }
    }

    private void createIncidentIfAbsent(
            ServerNode server,
            AlertSeverity severity,
            String title,
            String description) {

        boolean alreadyExists = incidents.existsByServer_IdAndTitleAndStatusIn(
                server.getId(),
                title,
                List.of(
                        AlertStatus.OPEN,
                        AlertStatus.ACKNOWLEDGED));

        if (!alreadyExists) {
            incidents.save(
                    new Incident(
                            server,
                            severity,
                            title,
                            description));
        }
    }

    private void resolveActiveIncidents(
            UUID serverId,
            List<String> titles) {

        List<AlertStatus> activeStatuses = List.of(
                AlertStatus.OPEN,
                AlertStatus.ACKNOWLEDGED);

        for (String title : titles) {

            List<Incident> activeIncidents = incidents.findAllByServer_IdAndTitleAndStatusIn(
                    serverId,
                    title,
                    activeStatuses);

            activeIncidents.forEach(Incident::resolve);

            incidents.saveAll(activeIncidents);
        }
    }

    public List<IncidentResponse> listIncidents() {
        return incidents.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(IncidentResponse::from)
                .toList();
    }

    public IncidentResponse acknowledge(UUID id) {

        Incident i = incidents.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Incident not found"));

        i.acknowledge();

        return IncidentResponse.from(
                incidents.save(i));
    }

    public List<Map<String, Object>> history(UUID serverId) {

        if (!servers.existsById(serverId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Server not found");
        }

        return metrics.findTop100ByServer_IdOrderByRecordedAtDesc(serverId)
                .stream()
                .sorted(Comparator.comparing(Metric::getRecordedAt))
                .map(m -> Map.<String, Object>of(
                        "recordedAt", m.getRecordedAt(),
                        "cpuPercent", m.getCpuPercent(),
                        "memoryPercent", m.getMemoryPercent()))
                .toList();
    }
}
