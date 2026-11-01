package com.sentinel.domain;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "monitoring_groups")
public class MonitoringGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private UUID ownerSessionId;

    protected MonitoringGroup() {
    }

    public MonitoringGroup(String name, UUID ownerSessionId) {
        this.name = name;
        this.ownerSessionId = ownerSessionId;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public UUID getOwnerSessionId() {
        return ownerSessionId;
    }
}
