package com.sentinel.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "servers")
public class ServerNode {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String host;

    @Column(nullable = false)
    private String environment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServerStatus status = ServerStatus.OFFLINE;

    private Double cpuPercent;
    private Double memoryPercent;
    private Instant lastHeartbeat;

    protected ServerNode() {
    }

    public ServerNode(String name, String host, String environment) {
        this.name = name;
        this.host = host;
        this.environment = environment;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getHost() {
        return host;
    }

    public String getEnvironment() {
        return environment;
    }

    public ServerStatus getStatus() {
        return status;
    }

    public Double getCpuPercent() {
        return cpuPercent;
    }

    public Double getMemoryPercent() {
        return memoryPercent;
    }

    public Instant getLastHeartbeat() {
        return lastHeartbeat;
    }

    public void updateDetails(
            String name,
            String host,
            String environment) {
        this.name = name;
        this.host = host;
        this.environment = environment;
    }

    public void updateMetrics(
            double cpu,
            double memory,
            Instant at,
            ServerStatus next) {
        this.cpuPercent = cpu;
        this.memoryPercent = memory;
        this.lastHeartbeat = at;
        this.status = next;
    }
}